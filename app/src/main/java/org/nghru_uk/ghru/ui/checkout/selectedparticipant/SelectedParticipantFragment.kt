package org.nghru_uk.ghru.ui.checkout.selectedparticipant

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.nghru_uk.ghru.*
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.SelectedParticipantFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.ui.checkout.notcomplete.NotCompleteDialogFragment
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.getLocalTimeString
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.request.CheckoutData
import org.nghru_uk.ghru.vo.request.CheckoutRequest
import org.nghru_uk.ghru.vo.request.CheckoutRequestUK
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList


class SelectedParticipantFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<SelectedParticipantFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var homeViewModel: SelectedParticipantViewModel

    @Inject
    lateinit var  jobManager: JobManager

    private var ParticipantStationsItem: ParticipantStationsItem? = null

    private var participantRequest: ParticipantRequest? = null

    private var isAllStationsCompleted: Boolean = false
    private var overallStatus: String? = ""
    //var meta: Meta? = null
    var user: User? = null

    private var BM_Status: String? = "Not started"
    private var BP_Status: String? = "Not started"
    private var SP_Status: String? = "Not started"
    private var SAM_Status: String? = "Not started"
    private var PA_QU_Status: String? = "Not started"
    private var ECG_Status: String? = "Not started"
    private var FUN_Status: String? = "Not started"
    private var REG_Status: String? = "Not started"
//    private var REP_Status: String? = "Not started"
    //private var ST_QU_Status: String? = "Not started"
    private var AX_Status: String? = "Not started"
    private var INT_Status: String? = "Not started"
    private var CHK_Status: String? = "Not started"

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
        val dataBinding = DataBindingUtil.inflate<SelectedParticipantFragmentBinding>(
            inflater,
            R.layout.selected_participant_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_setting -> {
//                val intent = Intent(activity, SettingActivity::class.java)
//                startActivity(intent)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        jobManager.stop()
        binding.homeViewModel = homeViewModel

//        homeViewModel.setUser("user")
//        homeViewModel.user?.observe(this, Observer {
//
//                userData ->
//            if (userData?.data != null)
//            {
//                user = userData.data
//
//                val sTime: String = convertTimeTo24Hours()
//                val sDate: String = getDate()
//                val sDateTime:String = sDate + " " + sTime
//
//                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
//                //meta?.registeredBy = user?.id
//            }
//        })

        if (participantRequest != null)
        {
            binding.participantId.setText(participantRequest!!.screeningId)
        }

        Log.d("HOME_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        binding.buttonCancel.singleClick {

            activity?.finish()
        }

        binding.progressBar.visibility = View.VISIBLE

        homeViewModel.setParticipantId(participant = participantRequest!!.screeningId!!)

        homeViewModel.getSingleParticipantStations?.observe(activity!!, Observer {

            if (isNetworkAvailable())
            {
                if (it.status.equals(Status.SUCCESS))
                {
                    val stationList: ArrayList<ParticipantStation> = ArrayList<ParticipantStation>()

                    it.data!!.data!!.stations?.forEach { stations ->

                        stationList.add(stations)
                    }

                    isAllStationsCompleted = findOverallStatusStatus(stationList)

                    Log.d("SELECTED_PARTICIPANT", "STATUS: " + isAllStationsCompleted)

                }
                else if (it.status == Status.ERROR)
                {
                    Log.d("SELECTED_PARTICIPANT", "ERROR: " + it.status)
                }
            }
            else
            {
                Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
            }
        })

        homeViewModel.chkPostComplete?.observe(this, Observer { chkPocess ->

            if (chkPocess?.status == Status.SUCCESS)
            {
                val bundle = bundleOf("ParticipantRequest" to participantRequest)
                findNavController().navigate(R.id.action_selectedParticipantFragment_to_CheckoutCompletionFragment, bundle)
            }
            else if(chkPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "CheckoutRequest",
                    CheckoutRequestUK(meta = participantRequest?.meta, comment = "").toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + chkPocess.message.toString()))
            }
        })

        binding.buttonSubmit.singleClick {

            if (isAllStationsCompleted)
            {
                if (participantRequest != null)
                {
                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)
                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val chkRequest = CheckoutRequestUK(meta = participantRequest?.meta, comment="")
                    chkRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        chkRequest.syncPending =false
                    }else{
                        chkRequest.syncPending =true

                    }

                    Log.d("FFQ_CONFIRAMTION", "DATA:" + chkRequest)

                    homeViewModel.setPostChk(chkRequest, participantRequest!!.screeningId)
                }
            }
            else
            {
                val notCompleteFragment = NotCompleteDialogFragment()
                notCompleteFragment.arguments = bundleOf("ParticipantRequest" to participantRequest, "Meta" to participantRequest?.meta)
                notCompleteFragment.show(fragmentManager!!)
            }
        }

        //binding.homeItem = ParticipantStationsItem
        //ParticipantStationsItem!!.participant_id = participantRequest!!.screeningId

    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        inflater?.inflate(R.menu.menu_main, menu)
//        //checkConnection(menu!!)
//    }

    private fun findOverallStatusStatus(stationList: ArrayList<ParticipantStation>): Boolean
    {
        for (station in stationList)
        {
            if (station.station_name == "Body Measurements")
            {
                if (station.isCancelled == 1)
                {
                    BM_Status = "Canceled"
                    binding.bodyIcon.background = resources.getDrawable(R.drawable.status_cancel)
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    BM_Status = "In Progress"
                    binding.bodyIcon.background = resources.getDrawable(R.drawable.status_progress)
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    BM_Status = "Completed"
                    binding.bodyIcon.background = resources.getDrawable(R.drawable.status_complete)
                }
                else
                {
                    BM_Status = "Not started"
                    binding.bodyIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Blood Pressure") {
                if (station.isCancelled == 1) {
                    BP_Status = "Canceled"
                    binding.bpIcon.background = resources.getDrawable(R.drawable.status_cancel)
                }
                else if (station.status_code!!.toInt() == 1) {
                    BP_Status = "In Progress"
                    binding.bpIcon.background = resources.getDrawable(R.drawable.status_progress)
                }
                else if (station.status_code!!.toInt() == 100) {
                    BP_Status = "Completed"
                    binding.bpIcon.background = resources.getDrawable(R.drawable.status_complete)
                }
                else {
                    BP_Status = "Not tsarted"
                    binding.bpIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Biological Samples") {
                if (station.isCancelled == 1) {
                    SAM_Status = "Canceled"
                    binding.sampleIcon.background = resources.getDrawable(R.drawable.status_cancel)
                }
                else if (station.status_code!!.toInt() == 1) {
                    SAM_Status = "In Progress"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                }
                else if (station.status_code!!.toInt() == 1000) {
                    SAM_Status = "In Progress"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                }
                else if (station.status_code!!.toInt() == 100) {
                    SAM_Status = "Completed"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                }
                else if (station.status_code!!.toInt() == 10000) {
                    SAM_Status = "Completed"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                }
                else {
                    SAM_Status = "Not started"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Spirometry") {
                if (station.isCancelled == 1) {
                    SP_Status = "Canceled"
                    binding.spiroIcon.background = resources.getDrawable(R.drawable.status_cancel)
                } else if (station.status_code!!.toInt() == 1) {
                    SP_Status = "In Progress"
                    binding.spiroIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                } else if (station.status_code!!.toInt() == 100) {
                    SP_Status = "Completed"
                    binding.spiroIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                } else {
                    SP_Status = "Not started"
                    binding.spiroIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
//        for (station in stationList) {
//            if (station.station_name == "Staff HLQ") {
//                if (station.isCancelled == 1) {
//                    ST_QU_Status = "Canceled"
//                    binding.hlqIcon.background = resources.getDrawable(R.drawable.status_cancel)
//                } else if (station.status_code!!.toInt() == 1) {
//                    ST_QU_Status = "In Progress"
//                    binding.hlqIcon.background =
//                        resources.getDrawable(R.drawable.status_progress)
//                } else if (station.status_code!!.toInt() == 100) {
//                    ST_QU_Status = "Completed"
//                    binding.hlqIcon.background =
//                        resources.getDrawable(R.drawable.status_complete)
//                } else {
//                    ST_QU_Status = "Not started"
//                    binding.hlqIcon.background =
//                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
//                }
//            }
//        }
        for (station in stationList) {
            if (station.station_name == "Participant HLQ") {
                if (station.isCancelled == 1) {
                    PA_QU_Status = "Canceled"
                    binding.selfIcon.background = resources.getDrawable(R.drawable.status_cancel)
                } else if (station.status_code!!.toInt() == 1) {
                    PA_QU_Status = "In Progress"
                    binding.selfIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                } else if (station.status_code!!.toInt() == 100) {
                    PA_QU_Status = "Completed"
                    binding.selfIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                } else {
                    PA_QU_Status = "Not started"
                    binding.selfIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Intake 24") {
                if (station.isCancelled == 1) {
                    INT_Status = "Canceled"
                    binding.intakeIcon.background = resources.getDrawable(R.drawable.status_cancel)
                } else if (station.status_code!!.toInt() == 1) {
                    INT_Status = "In Progress"
                    binding.intakeIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                } else if (station.status_code!!.toInt() == 100) {
                    INT_Status = "Completed"
                    binding.intakeIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                } else {
                    INT_Status = "Not started"
                    binding.intakeIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
//        for (station in stationList) {
//            if (station.station_name == "Report") {
//                if (station.isCancelled == 1) {
//                    REP_Status = "Canceled"
//                    binding.reportIcon.background = resources.getDrawable(R.drawable.status_cancel)
//                } else if (station.status_code!!.toInt() == 1) {
//                    REP_Status = "In Progress"
//                    binding.reportIcon.background =
//                        resources.getDrawable(R.drawable.status_progress)
//                } else if (station.status_code!!.toInt() == 100) {
//                    REP_Status = "Completed"
//                    binding.reportIcon.background =
//                        resources.getDrawable(R.drawable.status_complete)
//                } else {
//                    REP_Status = "Not started"
//                    binding.reportIcon.background =
//                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
//                }
//            }
//        }
        for (station in stationList) {
            if (station.station_name == "Fundoscopy") {
                if (station.isCancelled == 1) {
                    FUN_Status = "Canceled"
                    binding.fundoIcon.background = resources.getDrawable(R.drawable.status_cancel)
                } else if (station.status_code!!.toInt() == 1) {
                    FUN_Status = "In Progress"
                    binding.fundoIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                } else if (station.status_code!!.toInt() == 100) {
                    FUN_Status = "Completed"
                    binding.fundoIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                } else {
                    FUN_Status = "Not started"
                    binding.fundoIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Registration") {
                if (station.isCancelled == 1) {
                    REG_Status = "Canceled"
                    binding.registerIcon.background = resources.getDrawable(R.drawable.status_cancel)
                } else if (station.status_code!!.toInt() == 1) {
                    REG_Status = "In Progress"
                    binding.registerIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                } else if (station.status_code!!.toInt() == 100) {
                    REG_Status = "Completed"
                    binding.registerIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                } else {
                    REG_Status = "Not started"
                    binding.registerIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "ECG") {
                if (station.isCancelled == 1) {
                    ECG_Status = "Canceled"
                    binding.ecgIcon.background = resources.getDrawable(R.drawable.status_cancel)
                } else if (station.status_code!!.toInt() == 1) {
                    ECG_Status = "In Progress"
                    binding.ecgIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                } else if (station.status_code!!.toInt() == 100) {
                    ECG_Status = "Completed"
                    binding.ecgIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                } else {
                    ECG_Status = "Not started"
                    binding.ecgIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Checkout") {
                if (station.isCancelled == 1) {
                    CHK_Status = "Canceled"
                    binding.checkoutIcon.background = resources.getDrawable(R.drawable.status_cancel)
                } else if (station.status_code!!.toInt() == 1) {
                    CHK_Status = "In Progress"
                    binding.checkoutIcon.background =
                        resources.getDrawable(R.drawable.status_progress)
                } else if (station.status_code!!.toInt() == 100) {
                    CHK_Status = "Completed"
                    binding.checkoutIcon.background =
                        resources.getDrawable(R.drawable.status_complete)
                } else {
                    CHK_Status = "Not started"
                    binding.checkoutIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Axivity")
            {
                if (station.isCancelled == 1)
                {
                    AX_Status = "Canceled"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.status_cancel)
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    AX_Status = "In Progress"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.status_progress)
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    AX_Status = "Completed"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.status_complete)
                }
                else
                {
                    AX_Status = "Not tarted"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)
                }
            }
        }

        Log.d("MEASUREMENT_FRAGMENT", "STATION_STATUSES:"
                + "BM - "+ BM_Status
                + "BP - "+ BP_Status
                + "FBG - "+ SAM_Status
                + "SP - "+ SP_Status
                + "ECG - "+ ECG_Status
                + "AX - "+ AX_Status
                + "FUN - "+ FUN_Status
                + "REG - "+ REG_Status
                + "CHK - "+ CHK_Status
                + "INT - "+ INT_Status
                + "PA_QU - "+ PA_QU_Status)

        var overallStatus : Boolean

        if (BP_Status == "Completed"
            && BM_Status == "Completed"
            && SP_Status == "Completed"
            && SAM_Status == "Completed"
            && ECG_Status == "Completed"
            && AX_Status == "Completed"
            && FUN_Status == "Completed"
            && INT_Status == "Completed"
            && REG_Status == "Completed"
            && CHK_Status == "Completed"
            && PA_QU_Status == "Completed")
        {
            overallStatus = true
        }
        else
        {
            overallStatus = false
        }

        binding.progressBar.visibility = View.GONE
        return overallStatus
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

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
