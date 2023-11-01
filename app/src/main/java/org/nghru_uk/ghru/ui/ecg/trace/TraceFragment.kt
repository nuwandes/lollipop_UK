package org.nghru_uk.ghru.ui.ecg.trace


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
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
import com.crashlytics.android.Crashlytics
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.TraceFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.ui.ecg.trace.complete.CompleteDialogFragment
import org.nghru_uk.ghru.ui.ecg.trace.reason.ReasonDialogFragment
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Measurements
import org.nghru_uk.ghru.vo.StationDeviceData
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

import org.nghru_uk.ghru.ui.ecg.trace.completed.CompletedDialogFragment
import org.nghru_uk.ghru.util.getLocalTimeString
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TraceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<TraceFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var verifyIDViewModel: TraceViewModel

    private var participant: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<TraceFragmentBinding>(
            inflater,
            R.layout.trace_fragment,
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participant
        // binding.viewModel = verifyIDViewModel
        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participant)
            reasonDialogFragment.show(fragmentManager!!)
        }

        binding.buttonSubmit.singleClick {

            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else {

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participant!!.meta!!.endTime = endDateTime

                verifyIDViewModel.setECGRemote(participant!!, binding.comment.text.toString(), selectedDeviceID!!,isNetworkAvailable())
//                val completeDialogFragment = CompleteDialogFragment()
////            val bundle = Bundle()
////            bundle.putParcelable("participant", participant)
////            bundle.putString("comment", binding.comment.text.toString())
////            bundle.putString("deviceId",selectedDeviceID)
//                completeDialogFragment.arguments = bundleOf(
//                    "participant" to participant,
//                    "comment" to binding.comment.text.toString(),
//                    "deviceId" to selectedDeviceID
//                )
//                completeDialogFragment.show(fragmentManager!!)
            }
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        verifyIDViewModel.setStationName(Measurements.ECG)
        verifyIDViewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter.notifyDataSetChanged()
            }
        })
        binding.deviceIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
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

        verifyIDViewModel.eCGSaveRemote?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {

                Crashlytics.setString("comment", binding.comment.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("eCGSaveRemote " + participant.message.toString()))
                binding.progressBar.visibility = View.GONE
                binding.textViewError.setText(participant.message?.message)
                binding.textViewError.visibility = View.VISIBLE
                binding.executePendingBindings()
            }
        })
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

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
