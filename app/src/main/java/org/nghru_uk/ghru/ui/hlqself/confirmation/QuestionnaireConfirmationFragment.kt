package org.nghru_uk.ghru.ui.hlqself.confirmation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.Observer
import com.crashlytics.android.Crashlytics
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.QuestionnaireConfirmationFragmentBinding
import org.nghru_uk.ghru.databinding.QuestionnaireSelfConfirmationFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BusProvider
import org.nghru_uk.ghru.ui.bodymeasurements.bp.completed.CompletedDialogFragment
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.getLocalTimeString
import org.nghru_uk.ghru.util.hideKeyboard
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.HLQResponse
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class QuestionnaireConfirmationFragment : Fragment() , Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<QuestionnaireSelfConfirmationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: QuestionnaireConfirmationViewModel

    private var participantRequest: ParticipantRequest? = null

    private var hlqData: HLQResponse? = null

    //var meta: Meta? = null
    var user: User? = null

    private var assistanceRequired: Boolean? = false
    private var questionnaireCompleted: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<QuestionnaireSelfConfirmationFragmentBinding>(
            inflater,
            R.layout.questionnaire_self_confirmation_fragment,
            container,
            false
        )
        binding = dataBinding

        //setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participantRequest
        binding.viewModel = viewModel

        Log.d("CONFIRM_FRAG", "ON_LOADING: " + participantRequest!!.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        viewModel.hlqUpdateComplete?.observe(this, Observer { ffqPocess ->

            if(ffqPocess?.status == Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
            }

            if (ffqPocess?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
                //Toast.makeText(activity!!, getString(R.string.questionnaire_success), Toast.LENGTH_SHORT).show()
                //activity!!.finish()
            } else if(ffqPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "HLQUpdate",
                    hlqData!!.toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + ffqPocess.message.toString()))
            }
        })

        binding.buttonSubmit.singleClick {
            if (validateNextButton()) {

                Log.d("CONFIRM_FRAG", "BEFORE_ASSIGN: " + participantRequest!!.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

                Log.d("CONFIRM_FRAG", "AFTER_ASSIGN: " + participantRequest!!.meta + " END_TIME: " + participantRequest?.meta?.endTime)

//                meta = participantRequest?.meta
//                meta?.endTime = endDateTime

                hlqData = HLQResponse(
                    questionnaire_completed = questionnaireCompleted!!,
                    assistance_required = assistanceRequired!!,
                    meta  = participantRequest?.meta
                )

                viewModel.setUpdateHLQ(hlqData!!, participantRequest!!.screeningId)

                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE

            } else {

                binding.executePendingBindings()

            }
        }

        binding.radioGroupStaff.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noStaff) {
                binding.radioGroupStaffValue = false
                viewModel.setHaveStaff(false)
                questionnaireCompleted = false

            }
            else {
                binding.radioGroupStaffValue = false
                viewModel.setHaveStaff(true)
                questionnaireCompleted = true
            }
            binding.executePendingBindings()
        }

        binding.radioGroupAssistance.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noAssistance) {
                binding.radioGroupAssistanceValue = false
                viewModel.setHaveAssistance(false)
                assistanceRequired = false

            } else {
                binding.radioGroupAssistanceValue = false
                viewModel.setHaveAssistance(true)
                assistanceRequired = true
            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {

        if (viewModel.haveStaff.value == null)
        {
            binding.radioGroupStaffValue = true
            binding.executePendingBindings()
            return false
        }

        if (viewModel.haveAssistance.value == null)
        {
            binding.radioGroupAssistanceValue = true
            binding.executePendingBindings()
            return false
        }

//        if(viewModel.haveCommunicate.value ==  false) {
////            val skipDialogFragment = FoodFrequencySkipFragment()
////            skipDialogFragment.arguments = bundleOf("participant" to participantRequest,
////                "contraindications" to getContraindications(),
////                "type" to TYPE_FOOD_FREQUENCY)
////            skipDialogFragment.show(fragmentManager!!)
//
//            return false
//        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.root.hideKeyboard()
                navController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
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
        val date: Date
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
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            return output
        }catch(p: ParseException){
            return ""
        }
    }

}
