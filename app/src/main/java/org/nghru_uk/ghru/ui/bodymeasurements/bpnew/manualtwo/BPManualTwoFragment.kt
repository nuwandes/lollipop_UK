package org.nghru_uk.ghru.ui.bodymeasurements.bpnew.manualtwo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_uk.ghru.BuildConfig
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.BPManualTwoFragmentBinding
import org.nghru_uk.ghru.databinding.BPManualTwoFragmentNewBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BPRecordRxBus
import org.nghru_uk.ghru.util.Constants
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.hideKeyboard
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BPManualTwoFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var participant: ParticipantRequest? = null

    var binding by autoCleared<BPManualTwoFragmentNewBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var isValidRecord: Boolean = false
    @Inject
    lateinit var viewModel: BPManualTwoViewModel

    private var selectedArm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            selectedArm = arguments?.getString("SelectedArm")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<BPManualTwoFragmentNewBinding>(
            inflater,
            R.layout.b_p_manual_two_fragment_new,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)

        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this

        viewModel.getBloodPressure().value?.systolic?.observe(
            this,
            Observer { systolic ->
                validateSystolicBp(systolic!!)
                validateNextButton()
            })
        viewModel.getBloodPressure().value?.diastolic?.observe(
            this,
            Observer { diastolic ->
                validateDiatolicBp(diastolic!!)
                validateNextButton()
            })
        viewModel.getBloodPressure().value?.pulse?.observe(this, Observer { pluse ->
            validatePulse(pluse)
            validateNextButton()
        })

        viewModel.getBloodPressure().value?.arm?.value = selectedArm
        var position = 0
        if(selectedArm == "right") {
            position = 1
        }
        binding.armSwitch.checkedTogglePosition = position
        binding.armSwitch.setOnToggleSwitchChangeListener { position, isChecked ->
            if (position == 0) {
                viewModel.getBloodPressure().value?.arm?.value = "left"
            } else {
                viewModel.getBloodPressure().value?.arm?.value = "right"
            }
        };

        if (BuildConfig.DEBUG) {
//            viewModel.getBloodPressure().value?.arm?.value = "left"
//            viewModel.getBloodPressure().value?.systolic?.value = "120"
//            viewModel.getBloodPressure().value?.diastolic?.value = "90"
//            viewModel.getBloodPressure().value?.pulse?.value = "80"
        }

        binding.buttonClose.singleClick {
            view?.hideKeyboard()
            navController().popBackStack()
        }
        binding.buttonRecord.singleClick {
            validateNextButton()
            if (isValidRecord) {
                viewModel.bloodPressure?.value?.timestamp?.value = getTimeStamp()
                BPRecordRxBus.getInstance().post(viewModel.getBloodPressure().value!!)
                binding.root.hideKeyboard()
                navController().popBackStack()
            }
        }
        binding.bloodPressure = viewModel.getBloodPressure().value
    }

    private fun validateSystolicBp(systolic: String) {
        try {
            val systolicVal: Double = systolic.toDouble()
            if (systolicVal >= Constants.BP_SYSTOLIC_MIN_VAL && systolicVal <= Constants.BP_SYSTOLIC_MAX_VAL) {
                binding.systolicInputLayout.error = null
                viewModel.isValidSystolicBp = false

            } else {
                viewModel.isValidSystolicBp = true
                binding.systolicInputLayout.error = getString(R.string.error_not_in_range)
            }

            validateNextButton()

        } catch (e: Exception) {
            viewModel.isValidSystolicBp = true
            binding.systolicInputLayout.error = getString(R.string.error_invalid_input)
        }
    }

    private fun validateDiatolicBp(diatolic: String) {
        try {
            val diatolicVal: Double = diatolic.toDouble()
            if (diatolicVal >= Constants.BP_DIATOLIC_MIN_VAL && diatolicVal <= Constants.BP_DIATOLIC_MAX_VAL) {
                binding.diastolicInputLayout.error = null
                viewModel.isValidDiastolicBp = false
            } else {
                viewModel.isValidDiastolicBp = true
                binding.diastolicInputLayout.error = getString(R.string.error_not_in_range)
            }
            validateNextButton()
        } catch (e: Exception) {
            binding.diastolicInputLayout.error = getString(R.string.error_invalid_input)
        }
    }


    private fun validatePulse(pulse: String) {
        try {
            val pulseVal: Double = pulse.toDouble()
            if (pulseVal >= Constants.BP_PULSE_MIN_VAL && pulseVal <= Constants.BP_PULSE_MAX_VAL) {
                binding.pulseInputLayout.error = null
                viewModel.isValidPuls = false
            } else {
                viewModel.isValidPuls = true
                binding.pulseInputLayout.error = getString(R.string.error_not_in_range)
            }
            validateNextButton()
        } catch (e: Exception) {
            binding.pulseInputLayout.error = getString(R.string.error_invalid_input)
        }
    }


    private fun validateNextButton() {
        isValidRecord = (!binding.bloodPressure?.systolic?.value.isNullOrBlank()
                && !binding.bloodPressure?.diastolic?.value.isNullOrBlank()
                && !binding.bloodPressure?.pulse?.value.isNullOrBlank()
                && !viewModel.isValidDiastolicBp
                && !viewModel.isValidSystolicBp
                && !viewModel.isValidPuls)
    }

    private fun getTimeStamp(): String {
        val now: Calendar = Calendar.getInstance()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output: String
        return try {
            output = dateFormat.format(now.time)
            output
        } catch (p: ParseException) {
            ""
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}