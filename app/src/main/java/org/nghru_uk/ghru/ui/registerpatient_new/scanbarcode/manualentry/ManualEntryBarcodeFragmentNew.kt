package org.nghru_uk.ghru.ui.registerpatient_new.scanbarcode.manualentry

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
import androidx.navigation.fragment.findNavController
import io.reactivex.disposables.CompositeDisposable
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.RegisterPatientBarcodeManualentryFragmentBinding
import org.nghru_uk.ghru.databinding.RegisterPatientBarcodeManualentryFragmentNewBinding
import org.nghru_uk.ghru.databinding.RegisterPatientBarcodeManualentryFragmentSgBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.ui.codeheck.CodeCheckDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.request.ParticipantMeta
import javax.inject.Inject

class ManualEntryBarcodeFragmentNew : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<RegisterPatientBarcodeManualentryFragmentNewBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
//    @Inject
//    lateinit var scanbarcodeViewModel: ScanBarcodeViewModel

    private val disposables = CompositeDisposable()

    private var participantMeta: ParticipantMeta? = null

    private var concentPhoto: String? = null

    @Inject
    lateinit var viewModel: ManualEntryScanBarcodeViewModelNew

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantMeta = arguments?.getParcelable<ParticipantMeta>("participantMeta")!!
            concentPhoto = arguments?.getString("concentPhotoPath")!!
        } catch (e: KotlinNullPointerException) {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<RegisterPatientBarcodeManualentryFragmentNewBinding>(
            inflater,
            R.layout.register_patient_barcode_manualentry_fragment_new,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        binding.root.shoKeyboard()

        binding.buttonContinue.singleClick {

            handleContinue()
            view?.hideKeyboard()
        }
        binding.buttonBack.singleClick {

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

        viewModel.screeningIdCheck?.observe(this, Observer { householdId ->
            ////L.d(householdId.toString())
            if (householdId?.status == Status.SUCCESS) {
                val codeCheckDialogFragment = CodeCheckDialogFragment()
                codeCheckDialogFragment.show(fragmentManager!!)
            } else if (householdId?.status == Status.ERROR) {
                findNavController().navigate(
                    R.id.action_scanBarcodeManualFragmentNew_to_confirmationFragmentNew,
                    bundleOf("participantMeta" to participantMeta, "concentPhotoPath" to concentPhoto)
                )
            }

        })
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.editTextCode.filters = binding.editTextCode.filters + InputFilter.AllCaps()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.editTextCode.requestFocus()

    }

    fun handleContinue() {
        val checkSum = validateChecksum(binding.editTextCode.text.toString(), Constants.TYPE_PARTICIPANT)

        if (!checkSum.error) {
            activity?.runOnUiThread({
                participantMeta?.body?.screeningId = binding.editTextCode.text.toString()
                viewModel.setScreeningId(binding.editTextCode.text.toString())
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
        when (item.getItemId()) {
            android.R.id.home -> {
                findNavController().navigate(
                    R.id.action_global_reviewFragment, bundleOf("participantMeta" to participantMeta, "concentPhotoPath" to concentPhoto)
                )
            }
        }
        return true
    }

    fun navController() = findNavController()


}
