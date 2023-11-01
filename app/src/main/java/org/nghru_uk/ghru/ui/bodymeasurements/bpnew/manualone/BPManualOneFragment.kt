package org.nghru_uk.ghru.ui.bodymeasurements.bpnew.manualone


import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.BPManualOneFragmentNewBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BPRecordRxBus
import org.nghru_uk.ghru.ui.bodymeasurements.bp.reason.ReasonDialogFragment
import org.nghru_uk.ghru.ui.bodymeasurements.review.completed.CompletedDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.*
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BPManualOneFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<BPManualOneFragmentNewBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var bPManualOneViewModel: BPManualOneViewModel

    private var participantRequest: ParticipantRequest? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BPRecordAdapter
    private val disposables = CompositeDisposable()
    private var recordList: ArrayList<BloodPressure> = ArrayList()
    private var isCriticalRecordFound: Boolean = false

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null
    var user: User? = null
    //var meta: Meta? = null

    @Inject
    lateinit var jobManager: JobManager

    private var mBloodPressureMetaRequest: BloodPressureMetaRequestNew? = null

    var armList: MutableList<String> = arrayListOf()
    private var selectedArm: String? = null
    var cuffSizeList: MutableList<String> = arrayListOf()
    private var selectedCuffSize: String? = null
    var smokingList: MutableList<String> = arrayListOf()
    private var selectedSmoking: String? = null
    var caffeineList: MutableList<String> = arrayListOf()
    private var selectedCaffeine: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest =
                arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {

        }

        armList.add("Left")
        armList.add("Right")

        cuffSizeList.add("Small: 17 - 22cm")
        cuffSizeList.add("Standard: 22 - 32cm")
        cuffSizeList.add("Large: 32 - 42cm")

        smokingList.add("None")
        smokingList.add("0 - 30 Mins")
        smokingList.add("30 - 60 Mins")
        smokingList.add("60+ Mins")

        caffeineList.add("None")
        caffeineList.add("0 - 30 Mins")
        caffeineList.add("30 - 60 Mins")
        caffeineList.add("60+ Mins")

        selectedArm = armList[0]
        selectedCuffSize = cuffSizeList[0]
        selectedSmoking = smokingList[0]
        selectedCaffeine = caffeineList[0]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<BPManualOneFragmentNewBinding>(
            inflater,
            R.layout.b_p_manual_one_fragment_new,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.expandProcedure = false
        binding.linearLayoutPrepContainer.collapse()
        binding.linearLayoutMessageContainer.collapse()

        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

        adapter = BPRecordAdapter(recordList)
        binding.recyclerView.adapter = adapter

        adapter.setOnItemClickListener { it ->
            if (recordList.contains(it)) {
                showDeleteConfirmation(it)
            }
        }

        disposables.add(
            BPRecordRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    if (!recordList.contains(result)) {
                        result.cuffSize.value = selectedCuffSize
                        recordList.add(result)
                        adapter.notifyDataSetChanged()

                        if (result.systolic.value?.toInt()!! > 180 || result.diastolic.value?.toInt()!! > 120) {
                            isCriticalRecordFound = true
                        }
                    }

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )
        disposables.add(
            BPRecordRxBus.getInstance().toObservableReset()
                .subscribe({ result ->

                    if (result == 1) {
                        recordList.clear()
                        adapter.notifyDataSetChanged()
                        isCriticalRecordFound = false
                    }

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this

//        bPManualOneViewModel.setUser("user")
//        bPManualOneViewModel.user?.observe(this, Observer { userData ->
//            if (userData?.data != null) {
//                val sTime: String = convertTimeTo24Hours()
//                val sDate: String = getDate()
//                val sDateTime = "$sDate $sTime"
//
//                user = userData.data
//                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
//            }
//
//        })

        binding.participant = participantRequest
        binding.bloodPressure = bPManualOneViewModel.getBloodPressure().value

        if (recordList.count() > 0) {
            binding.linearLayoutMessageContainer.expand()
        } else {
            binding.linearLayoutMessageContainer.collapse()
        }
        validateNextButton()
        binding.executePendingBindings()
        Log.d("BP_FRAGMENT", "META_DATA: BEFORE_CLICK" + participantRequest!!.meta!! + "END_TIME: " + participantRequest!!.meta!!.endTime)
        binding.nextButton.singleClick {

            if (selectedDeviceID == null) {
                binding.textViewDeviceError.visibility = View.VISIBLE
            } else {
                val bloodPresureRequestList: ArrayList<BloodPresureItemRequestNew> = ArrayList()
                recordList?.forEach {
                    val mBloodPressureItemRequest =
                        BloodPresureItemRequestNew(
                            systolic = it.systolic.value?.toInt()!!,
                            diastolic = it.diastolic.value?.toInt()!!,
                            pulse = it.pulse.value?.toInt()!!,
//                            arm = it.arm.value?.toString()!!,
//                            cuff_size = it.cuffSize.value?.toString()!!,
                            timestamp = it.timestamp.value?.toString()!!
                        )
                    bloodPresureRequestList.add(mBloodPressureItemRequest)
                }
                val mBloodPressureRequest = BloodPresureRequestNew(
                    comment = binding.comment.text.toString(),
                    device_id = selectedDeviceID.toString()
                )
                mBloodPressureRequest.syncPending = !isNetworkAvailable()
                mBloodPressureRequest.screeningId = participantRequest?.screeningId!!
                mBloodPressureRequest.bloodPresureRequestList = bloodPresureRequestList

                mBloodPressureRequest.arm = selectedArm!!
                mBloodPressureRequest.cuffSize = selectedCuffSize!!
                mBloodPressureRequest.smoking = selectedSmoking!!
                mBloodPressureRequest.caffeineConsumption = selectedCaffeine!!

                Log.d("BP_FRAGMENT", "META_DATA: BEFORE_ASSIGN" + participantRequest!!.meta!! + "END_TIME: " + participantRequest!!.meta!!.endTime)

//                val eTime: String = convertTimeTo24Hours()
//                val eDate: String = getDate()
//                val eDateTime = "$eDate $eTime"
//
//                meta?.endTime = eDateTime

                val eTime: String = convertTimeTo24Hours()
                val eDate: String = getDate()
                val eDateTime:String = eDate + " " + eTime

                participantRequest!!.meta!!.endTime = eDateTime

                Log.d("BP_FRAGMENT", "META_DATA: AFTER_ASSIGN" + participantRequest!!.meta!! + "END_TIME: " + participantRequest!!.meta!!.endTime)


                mBloodPressureMetaRequest =
                    BloodPressureMetaRequestNew(meta = participantRequest!!.meta!!, body = mBloodPressureRequest)

                mBloodPressureMetaRequest?.syncPending = !isNetworkAvailable()

                bPManualOneViewModel.setBloodPressureMetaRequestRemote(
                    mBloodPressureMetaRequest!!,
                    participantRequest!!
                )
                binding.progressBar.visibility = View.VISIBLE
                binding.textViewError.text = ""
                binding.textViewError.visibility = View.GONE
            }
        }

        bPManualOneViewModel.bloodPressureRequestRemote?.observe(this, Observer {
            binding.progressBar.visibility = View.GONE
            if (it.status.equals(Status.SUCCESS)) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.show(fragmentManager!!)
            } else if (it?.status == Status.ERROR) {
                Crashlytics.setString(
                    "mBloodPressureMetaRequest",
                    mBloodPressureMetaRequest.toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("bloodPressureRequestRemote " + it.message.toString()))
                binding.textViewError.visibility = View.VISIBLE
                binding.textViewError.setText(it.message?.message)
            }
        })

        binding.textViewSkip.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participantRequest,
                "skipped" to false
            )
            reasonDialogFragment.show(fragmentManager!!)
        }

        binding.previousButton.singleClick {
            navController().popBackStack()
        }

        binding.prepEC.setOnClickListener {
            if (binding.expandProcedure!!) {
                binding.linearLayoutPrepContainer.collapse()
                binding.expandProcedure = false
            } else {
                binding.linearLayoutPrepContainer.expand()
                binding.expandProcedure = true
            }
            binding.executePendingBindings()
        }

        binding.buttonGetReading.setOnClickListener {
            binding.textViewDeviceError.visibility = View.GONE
            val fmIntent = activity?.packageManager?.getLaunchIntentForPackage("org.omron.ghru")
            if (fmIntent is Intent) {
                fmIntent.flags = 0;
                fmIntent.putExtra("participant_id", participantRequest?.screeningId)
                fmIntent.putExtra("dob", participantRequest?.age?.dob)
                fmIntent.putExtra("gender", participantRequest?.gender)
                fmIntent.putExtra("arm", selectedArm)
                startActivityForResult(fmIntent, 100)
            }
            else {
                binding.textViewOmronError.visibility = View.VISIBLE
            }
        }

        binding.buttonAddTest.singleClick {
            val bundle = bundleOf(
                "ParticipantRequest" to participantRequest,
                Constants.ARG_BODY_MEASURMENT to bPManualOneViewModel.getBodyMeasurement().value,
                "SelectedArm" to selectedArm)
            navController().navigate(R.id.action_pPManualOneFragment_to_bPManualTwoFragment, bundle)
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.adapter = adapter;

        bPManualOneViewModel.setStationName(Measurements.BLOOD_PRESSURE)
        bPManualOneViewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter.notifyDataSetChanged()
            }
        })
        binding.deviceIdSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    @NonNull selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        selectedDeviceID = null
                    } else {
                        binding.textViewDeviceError.visibility = View.GONE
                        selectedDeviceID = deviceListObject[position - 1].device_id
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    // your code here
                }
            }

        val armAdapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, armList)
        binding.armSpinner.adapter = armAdapter;
        binding.armSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedArm = armList[position].toLowerCase()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }

        val cuffSizeAdapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, cuffSizeList)
        binding.cuffSizeSpinner.adapter = cuffSizeAdapter;
        binding.cuffSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedCuffSize = cuffSizeList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }

        val caffeineAdapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, caffeineList)
        binding.caffeineSpinner.adapter = caffeineAdapter;
        binding.caffeineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedCaffeine = caffeineList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }

        val smokingAdapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, smokingList)
        binding.smokingSpinner.adapter = smokingAdapter;
        binding.smokingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedSmoking = smokingList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }
    }

    private fun showDeleteConfirmation(bloodPressure: BloodPressure) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder!!.setMessage("Are you sure you want to delete this measurement?")

        builder.apply {
            setPositiveButton(R.string.app_yes) { dialog, _ ->
                recordList.remove(bloodPressure)
                adapter.notifyDataSetChanged()
                validateNextButton()
                dialog.dismiss()
            }
            setNegativeButton(R.string.app_no) { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog: AlertDialog? = builder.create()

        dialog!!.show()
    }

    private fun validateNextButton() {

        if (recordList.count() > 2) {
            binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
            binding.nextButton.setDrawableRightColor("#0A1D53")
            binding.nextButton.isEnabled = true
        } else {
            binding.nextButton.setTextColor(Color.parseColor("#AED6F1"));
            binding.nextButton.setDrawableRightColor("#AED6F1")
            binding.nextButton.isEnabled = false
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun convertTimeTo24Hours(): String {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: Date
        val output: String
        return try {
            date = inputFormat.parse(now.time.toLocaleString())
            output = outputformat.format(date)
            output
        } catch (p: ParseException) {
            ""
        }
    }

    private fun getDate(): String {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val outputformat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: Date
        val output: String
        return try {
            date = inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            output
        } catch (p: ParseException) {
            ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            val measurements =
                data.getSerializableExtra("measurements") as ArrayList<HashMap<String, String>>

            measurements.forEach {
                val bp = BloodPressure(0)
                bp.systolic.value = it.get("systolic")
                bp.diastolic.value = it.get("diastolic")
                bp.pulse.value = it.get("pulse_rate")
                bp.timestamp.value = it.get("timestamp")
                bp.arm.value = selectedArm
                bp.cuffSize.value = selectedCuffSize

                recordList.add(bp)
            }

            adapter.notifyDataSetChanged()
            validateNextButton()
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}