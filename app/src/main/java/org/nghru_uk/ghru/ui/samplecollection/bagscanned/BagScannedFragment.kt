package org.nghru_uk.ghru.ui.samplecollection.bagscanned

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.BagScannedFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.jobs.SyncSampledRequestJob
import org.nghru_uk.ghru.ui.samplecollection.bagscanned.completed.CompletedDialogFragment
import org.nghru_uk.ghru.ui.samplecollection.bagscanned.reason.ReasonDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.Comment
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.User
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import org.nghru_uk.ghru.vo.request.SampleCreateRequest
import org.nghru_uk.ghru.vo.request.SampleRequest
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import org.nghru_uk.ghru.vo.Date

class BagScannedFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<BagScannedFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var participant: ParticipantRequest? = null
    private var sampleId: String? = null

    @Inject
    lateinit var viewModel: BagScannedViewModel
    @Inject
    lateinit var jobManager: JobManager

    var allSampleCollected: Boolean = false

    var user: User? = null
    var cal = Calendar.getInstance()
    val sdf:DateFormat = SimpleDateFormat("yyyy-MM-dd")
    var selectedTime : String? = null
    var selectedDate : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            sampleId = arguments?.getString("sample_id")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<BagScannedFragmentBinding>(
            inflater,
            R.layout.bag_scanned_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()
        return dataBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.expand = true
        binding.linearLayoutEcContainer.collapse()
        binding.buttonCancel.singleClick {

            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participant)
            reasonDialogFragment.show(fragmentManager!!)
        }


        viewModel.setUser("user")
        viewModel.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {
                user = userData.data
            }
        })

        Log.d("BAG_SCANNED_FRAG", "ONLOAD_META: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

        binding.buttonSubmit.singleClick {

            if (allSampleCollected)
            {
                if (validateDateTime())
                {
                    Timber.d("participant $participant sample_id $sampleId")
                    val sampleRequest = SampleRequest(
                        screeningId = participant?.screeningId!!,
                        sampleId = sampleId!!,
                        comment = Comment(comment = binding.comment.text.toString())
                    )

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participant?.meta?.endTime = endDateTime

                    sampleRequest.meta = participant?.meta
                    sampleRequest.syncPending = !isNetworkAvailable()
                    sampleRequest.collectedBy = user?.name
                    sampleRequest.createdAt = binding.root.getLocalDateString()
                    sampleRequest.statusCode = 1
                    sampleRequest.syncPending = !isNetworkAvailable()
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                    viewModel.setSampleLocal(sampleRequest)

                    binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check, null)
                }
            } else {
                binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check_error, null)
            }

        }


        viewModel.sampleRequestLocal?.observe(this, Observer { sampleResource ->
            if (sampleResource?.status == Status.SUCCESS) {

                Log.d("BAG_SCANNED_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                val eTime: String = convertTimeTo24Hours()
                val eDate: String = getDate()
                val eDateTime:String = eDate + " " + eTime

                //println(user)
                sampleResource.data?.meta = participant?.meta
                sampleResource.data?.meta?.endTime = eDateTime

                Log.d("BAG_SCANNED_FRAG", "AFTER_ASSIGN: " + sampleResource.data?.meta + " END_TIME: " + sampleResource.data?.meta?.endTime)

                var mSampleCreateRequest = SampleCreateRequest(
                    meta =  sampleResource.data?.meta,
                    comment  = binding.comment.text.toString())
                mSampleCreateRequest.question = getQuestions()

                if (!isNetworkAvailable()) {
                    jobManager.addJobInBackground(SyncSampledRequestJob(sampleRequest = sampleResource.data!!,sampleCreateRequest = mSampleCreateRequest))
                    val completedDialogFragment = CompletedDialogFragment()
                    completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                    completedDialogFragment.show(fragmentManager!!)
                } else {
                    viewModel.setSample(participant, sampleId!!,mSampleCreateRequest)
                }

            } else if (sampleResource?.status == Status.ERROR) {
                //Crashlytics.logException(Exception(sampleResource.message?.message))
            }
        })

        viewModel.sample?.observe(this, Observer { sampleResource ->


            if (sampleResource?.status == Status.SUCCESS) {
                //println(user)
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (sampleResource?.status == Status.ERROR) {
                Crashlytics.setString("sampleId", sampleId.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("sample collection " + sampleResource.message.toString()))
                binding.buttonSubmit.visibility = View.GONE
                binding.textViewError.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.textViewError.text = sampleResource.message?.message
                //Crashlytics.logException(Exception(sampleResource.message?.message))
            }
        })


        binding.imageButtonEC.singleClick {
            if (binding.expand!!) {

                //collapse(binding.linearLayoutEcContainer)
                binding.linearLayoutEcContainer.collapse()
                binding.expand = false

            } else {
                //itexpand()
                binding.linearLayoutEcContainer.expand()
                binding.expand = true
            }
        }
        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

            binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check, null)
            allSampleCollected = isChecked
        }

        binding.participant = participant

        binding.lastMealDate.singleClick {

            val dialog = datePickerDialog()
            dialog.show()

            dialog.datePicker.setCalendarViewShown(false)
            dialog.datePicker.setSpinnersShown(true)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, 0)
            val rt = System.currentTimeMillis()-(1000L*60*60*8)
            Log.d("SAPLE_COLLECTION", "CURRENT: " + calendar.timeInMillis + ", 8 HOURS BACK" + rt)
            dialog.datePicker.maxDate = rt
        }

        binding.lastMealTime.singleClick {

            getTime()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun convertTimeTo24Hours(): String
    {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: java.util.Date
        val output: String
        try{
            date= inputFormat.parse(now.time.toLocaleString())
            output = outputformat.format(date)
            return output
        }catch(p: ParseException){
            return ""
        }
    }

    private fun getDate(): String
    {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val outputformat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: java.util.Date
        val output: String
        try{
            date= inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            return output
        }catch(p: ParseException){
            return ""
        }
    }

    @SuppressLint("SetTextI18n")
    fun datePickerDialog(): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(context!!, R.style.datepicker, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.mealYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val birthDate: Date = Date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
            viewModel.mealDate.postValue(sdf.format(cal.time))
            viewModel.mealDateVal.postValue(birthDate)
            viewModel.mealYearVal.postValue(year.toString())

            binding.lastMealDate.setText(sdf.format(cal.time))
            selectedDate = sdf.format(cal.time)
            binding.executePendingBindings()
        }, year, month, day)
        // Show Date Picker

        return datePickerDialog


    }

    @SuppressLint("SetTextI18m")
    fun getTime()
    {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val min = c.get(Calendar.MINUTE)

        // Time Picker Dialog
        val timePickerDialog = TimePickerDialog(context!!, R.style.datepicker, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)

            var ho:String? = null
            var min:String? = null

            if (hourOfDay >= 10)
            {
                ho =  hourOfDay.toString()
            }
            else
            {
                ho =  "0"+ hourOfDay.toString()
            }

            if (minute >= 10)
            {
                min =  minute.toString()
            }
            else
            {
                min =  "0"+ minute.toString()
            }

            binding.lastMealTime.setText(ho + ":" + min)
            selectedTime = ho + ":" + min

//            binding.lastMealTime.setText(hourOfDay.toString() + ":" + minute.toString())
//            selectedTime = hourOfDay.toString() + ":" + minute.toString()
            binding.executePendingBindings()
        }, hour, min, false)

        timePickerDialog.show()

    }

    fun validateDateTime() : Boolean
    {
        if (selectedDate == null)
        {
            Toast.makeText(context!!, "Please select date", Toast.LENGTH_LONG).show()
            return false
        }
        else if (selectedTime == null)
        {
            Toast.makeText(context!!, "Please select time", Toast.LENGTH_LONG).show()
            return false
        }
        else{
            return true
        }
    }

    private fun getQuestions(): MutableList<Map<String, String>> {
        var questions: MutableList<Map<String, String>> = mutableListOf()

        var questionMap = mutableMapOf<String, String>()
        questionMap["question"] = getString(R.string.bp_question_1)
        questionMap["answer"] = selectedDate + " " + selectedTime

        questions.add(questionMap)

        return questions
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home ->
            {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
