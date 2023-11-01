package org.nghru_uk.ghru.ui.registerpatient.scanqrcode.manualentry


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
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.RegisterPatientQrcodeManualentryFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.ScanQRCodeViewModel
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.membersdialog.MembersDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.request.HouseholdRequest
import org.nghru_uk.ghru.vo.request.Member
import javax.inject.Inject

class ManualEntryQRCodeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<RegisterPatientQrcodeManualentryFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ScanQRCodeViewModel
    var householdId: String = ""


    var meta: Meta? = null

    //var hoursFasted: String? = null

    var memberList: java.util.ArrayList<Member>? = null

    var household: HouseholdRequest? = null
    var membersResourceList: List<Member>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            meta = arguments?.getParcelable<Meta>("meta")!!
            //hoursFasted = arguments?.getString("hours_fasted")
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<RegisterPatientQrcodeManualentryFragmentBinding>(
            inflater,
            R.layout.register_patient_qrcode_manualentry_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.root.shoKeyboard()

        //Log.d("MANUAL_ENTRY_QR_CODE", "GOT IT")

        binding.buttonContinue.singleClick {

            handleContinue()
            view?.hideKeyboard()
        }
        binding.buttonBack.singleClick {

            navController().popBackStack()
            view?.hideKeyboard()
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

//        binding.root.shoKeyboard()
//        view?.shoKeyboard()

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("MANUAL_ENTRY_QR_CODE", "GOT IT")

//        binding.root.shoKeyboard()
//        view?.shoKeyboard()

        viewModel.members?.observe(this, Observer { membersResource ->
            binding.resource = membersResource
            if (membersResource?.status == Status.SUCCESS) {
                if (membersResource.data?.data?.isNotEmpty()!!) {
                    memberList = ArrayList(membersResource.data?.data)
                } else {
                    memberList = ArrayList()
                }
                viewModel.setEnumarationId(householdId)
            } else if (membersResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(membersResource.message?.message!!)
                errorDialogFragment.show(fragmentManager!!)
            }
            binding.executePendingBindings()

        })
        viewModel.houseHoldBody?.observe(this, Observer {

            household = it?.data?.data?.household
            if (household != null) {
                val membersDialogFragment = MembersDialogFragment()
                membersDialogFragment.arguments = bundleOf(
                    "householdId" to householdId,
                    "memberList" to memberList,
                    "meta" to meta,
                    "household" to household
                )
                membersDialogFragment.show(fragmentManager!!)
            }
        })

        viewModel.membersOfline?.observe(this, Observer { membersResource ->
            binding.resource = membersResource
            if (membersResource?.status == Status.SUCCESS) {
                if (membersResource.data?.isNotEmpty()!!) {
                    membersResourceList = membersResource.data;
                    viewModel.setEnumarationIdOffline(householdId)
                }
            } else if (membersResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage("The Participants ID is not found")
                errorDialogFragment.show(fragmentManager!!)
            }
            binding.executePendingBindings()

        })

        viewModel.houseHoldBodyOffline?.observe(this, Observer {

            household = it?.data?.householdRequest
            if (household != null) {
                val membersDialogFragment = MembersDialogFragment()
                membersDialogFragment.arguments = bundleOf(
                    "householdId" to householdId,
                    "memberList" to ArrayList(membersResourceList!!),
                    "meta" to meta,
                    "household" to household
                )
                membersDialogFragment.show(fragmentManager!!)
            }
        })


        binding.editTextCode.filters = binding.editTextCode.filters + InputFilter.AllCaps()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.editTextCode.requestFocus()

        binding.editTextCode.singleClick {

            Log.d("MANUAL_ENTRY_QR_CODE", "CLICKED")
            //binding.root.shoKeyboard()
            //view?.shoKeyboard()
        }


    }

    fun handleContinue() {
        val checkSum = validateChecksum(binding.editTextCode.text.toString(), Constants.TYPE_ENUMERATION)
        if (!checkSum.error) {
            householdId = binding.editTextCode.text.toString()
            if (isNetworkAvailable()) {
                viewModel.setHouseholdId(householdId)
            } else {
                viewModel.setHouseholdIdOffline(householdId)

            }

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
}