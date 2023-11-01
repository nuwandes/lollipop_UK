package org.nghru_uk.ghru.ui.checkout.bankdetails

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.BankDetailsFragmentBinding
import org.nghru_uk.ghru.databinding.CheckoutCompletionFragmentBinding
import org.nghru_uk.ghru.databinding.ManualEntryBodyMeasurementFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BusProvider
import org.nghru_uk.ghru.event.StationCheckRxBus
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.AlreadyCheckoutDialogFragment
//import org.nghru_uk.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment
//import org.nghru_uk.ghru.ui.statuscheck.StatusCheckDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.User
import org.nghru_uk.ghru.vo.request.CheckoutData
import org.nghru_uk.ghru.vo.request.CheckoutRequest
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class BankDetailsFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<BankDetailsFragmentBinding>()

    @Inject
    lateinit var viewModel: BankDetailsViewModel

    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    var status_code: String = ""
    var meta: Meta? = null
    var user: User? = null
    private var chkData: CheckoutData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //meta = arguments?.getParcelable<Meta>("meta")!!
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<BankDetailsFragmentBinding>(
            inflater,
            R.layout.bank_details_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        //appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.setLifecycleOwner(this)

        viewModel.setUser("user")
        viewModel.user?.observe(this, Observer {

                userData ->
            if (userData?.data != null)
            {
                user = userData.data

                val sTime: String = convertTimeTo24Hours()
                val sDate: String = getDate()
                val sDateTime:String = sDate + " " + sTime

                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
                //meta?.registeredBy = user?.id
            }
        })

        binding.buttonSubmit.singleClick {

            if (participantRequest != null)
            {
                if (!binding.accountHolderEditText.text.toString().equals("") && !binding.bankEditText.text.toString().equals("") && !binding.accountNumberEditText.text.toString().equals(""))
                {
                    chkData = CheckoutData(
                        holder_name = binding.accountHolderEditText.text.toString(),
                        bank_name = binding.bankEditText.text.toString(),
                        account_number = binding.accountNumberEditText.text.toString()
                    )

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    meta?.endTime = endDateTime

                    val chkRequest = CheckoutRequest(meta = meta, bank_details = chkData, comment="")
                    chkRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        chkRequest.syncPending =false
                    }else{
                        chkRequest.syncPending =true

                    }

                    Log.d("FFQ_CONFIRAMTION", "DATA:" + chkRequest)

                    viewModel.setPostChk(chkRequest, participantRequest!!.screeningId)
                }
//                val bundle = bundleOf("ParticipantRequest" to participantRequest)
//                findNavController().navigate(R.id.action_global_PaymentCompletionFragment, bundle)
            }
        }

        viewModel.chkPostComplete?.observe(this, Observer { chkPocess ->

            if (chkPocess?.status == Status.SUCCESS)
            {
                val bundle = bundleOf("ParticipantRequest" to participantRequest)
                findNavController().navigate(R.id.action_global_PaymentCompletionFragment, bundle)
            }
            else if(chkPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "CheckoutRequest",
                    CheckoutRequest(meta = meta, bank_details = chkData, comment = "").toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + chkPocess.message.toString()))
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

    fun navController() = findNavController()

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }
}