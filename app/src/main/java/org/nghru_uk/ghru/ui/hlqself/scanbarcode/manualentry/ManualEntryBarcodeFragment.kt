package org.nghru_uk.ghru.ui.hlqself.scanbarcode.manualentry

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import io.reactivex.disposables.CompositeDisposable
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.RegisterPatientBarcodeManualentryFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.StationCheckRxBus
import org.nghru_uk.ghru.ui.hlqself.scanbarcode.ScanBarcodeViewModel
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.QuestionnaireSelf
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ManualEntryBarcodeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<RegisterPatientBarcodeManualentryFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var scanbarcodeViewModel: ScanBarcodeViewModel


    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    var meta: Meta? = null

    private var questionnaire: QuestionnaireSelf? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            meta = arguments?.getParcelable<Meta>("meta")!!
            questionnaire = arguments?.getParcelable<QuestionnaireSelf>("Questionnaire")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        disposables.add(
            StationCheckRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    val bundle = bundleOf("ParticipantRequest" to participantRequest, "Questionnaire" to questionnaire)
                    Navigation.findNavController(activity!!, R.id.container)
                        .navigate(R.id.action_ManualEntryBarcodeFragment_to_WebFragment, bundle)
                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<RegisterPatientBarcodeManualentryFragmentBinding>(
            inflater,
            R.layout.register_patient_barcode_manualentry_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonContinue.singleClick {

            handleContinue()
            view?.hideKeyboard()
        }
        binding.buttonBack.singleClick {
            binding.root.hideKeyboard()
            navController().popBackStack()
        }
        binding.editTextCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textLayoutCode.error = ""
            }
        })
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        scanbarcodeViewModel.setUser("user")
//        scanbarcodeViewModel.user?.observe(this, Observer { userData ->
//            if (userData?.data != null) {
//
//                val sTime: String = convertTimeTo24Hours()
//                val sDate: String = getDate()
//                val sDateTime:String = sDate + " " + sTime
//
//                meta = Meta(collectedBy = userData.data?.id, startTime = sDateTime)
//                meta?.registeredBy = userData.data?.id
//            }
//
//        })

        binding.editTextCode.filters = binding.editTextCode.filters + InputFilter.AllCaps()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.editTextCode.requestFocus()


        scanbarcodeViewModel.participant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {

                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta
                if (!participantResource.data?.stationStatus!!) {
                    val bundle = bundleOf("ParticipantRequest" to participantRequest, "Questionnaire" to questionnaire)
                    Navigation.findNavController(activity!!, R.id.container)
                        .navigate(R.id.action_ManualEntryBarcodeFragment_to_WebFragment, bundle)
                } else {
                    val stationCheckDialogFragment = StationCheckDialogFragment()
                    stationCheckDialogFragment.show(fragmentManager!!)
                }

            } else if (participantResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()

                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
                errorDialogFragment.show(fragmentManager!!)
                //Crashlytics.logException(Exception(participantResource.toString()))
            }
            binding.executePendingBindings()
        })

//        scanbarcodeViewModel.participantOffline?.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participantRequest = participantResource.data
//                participantRequest?.meta = meta
//                val bundle = bundleOf("ParticipantRequest" to participantRequest, "Questionnaire" to questionnaire)
//                findNavController().navigate(R.id.action_ManualEntryBarcodeFragment_to_WebFragment, bundle)
//            } else if (participantResource?.status == Status.ERROR) {
//
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.setErrorMessage("The Paticipant ID is not found")
//                errorDialogFragment.show(fragmentManager!!)
//                //Crashlytics.logException(Exception(participantResource.toString()))
//            }
//            binding.executePendingBindings()
//        })

    }

    fun handleContinue() {
        val checkSum = validateChecksum(binding.editTextCode.text.toString(), Constants.TYPE_PARTICIPANT)
        if (!checkSum.error) {
            activity?.runOnUiThread({

                if (isNetworkAvailable()) {
                    scanbarcodeViewModel.setScreeningId(binding.editTextCode.text.toString())
                } else {
                    //scanbarcodeViewModel.setScreeningIdOffline(binding.editTextCode.text.toString())
                }
            })
        } else {

            binding.textLayoutCode.error = getString(R.string.invalid_code)//checkSum.message
        }
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

}