package org.nghru_uk.ghru.ui.registerpatient_sg.review

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.ReviewPatientFragmentSgBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.util.Constants
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.hideKeyboard
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Date
import org.nghru_uk.ghru.vo.request.ParticipantMeta
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ReviewFragmentSG : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ReviewPatientFragmentSgBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var reviewViewModelSG: ReviewViewModelSG

//    lateinit var participantMeta: ParticipantMeta

    var participantMeta: ParticipantMeta? = null

//    private var participantMeta: ParticipantMeta? = null

    val sdf = SimpleDateFormat(Constants.dataFormatOLD, Locale.US)

    var cal = Calendar.getInstance()

    private var concentPhoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantMeta = arguments?.getParcelable<ParticipantMeta>("participantMeta")!!
            concentPhoto = arguments?.getString("concentPhotoPath")!!
            //participantMeta = arguments?.getString("participantMeta")!!

        } catch (e: KotlinNullPointerException) {
            Log.d("EXCEPTION", "IS: " + e.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ReviewPatientFragmentSgBinding>(
            inflater,
            R.layout.review_patient_fragment_sg,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.member = participantMeta
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.root.hideKeyboard()
        binding.viewModel = reviewViewModelSG



        //val options = BitmapFactory.Options()
       // options.inSampleSize = 8
      //  val b = BitmapFactory.decodeFile(participantMeta?.body?.identityImage, options)

        // binding.userPhoto.setImageBitmap(b)
        binding.viewModel?.gender?.postValue(participantMeta?.body?.gender)

        if (participantMeta?.body?.age?.dob != null) {
            val date = SimpleDateFormat(Constants.dataFormatOLD, Locale.US).parse(participantMeta?.body?.age?.dob!!)
            reviewViewModelSG.birthDateVal.postValue(Date(date?.year!!, date.month, date?.date))
            binding.viewModel?.birthDate?.postValue(participantMeta?.body?.age?.dob!!)
        }

        if (participantMeta?.body?.age != null) {
            binding.viewModel?.age?.postValue(participantMeta?.body?.age?.ageInYears)
        }

        reviewViewModelSG.gender.observe(this, androidx.lifecycle.Observer { gender ->
            participantMeta?.body?.gender = gender.toString().toUpperCase()
            // Log.d("Gender >>",gender.toString().toUpperCase())
        })


        binding.nextButton.singleClick {
            binding.root.hideKeyboard()
            Timber.d(participantMeta.toString())
            navController().navigate(
                R.id.action_reviewFragmentSG_to_scanBarcodeFragmentSG,
                bundleOf("participantMeta" to participantMeta)
            )
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            reviewViewModelSG.birthYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val birthDate: Date =
                Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
            reviewViewModelSG.birthDate.postValue(sdf.format(cal.time))
            reviewViewModelSG.birthDateVal.postValue(birthDate)
            val years = Calendar.getInstance().get(Calendar.YEAR) - year
            reviewViewModelSG.age.value = years.toString()

            binding.executePendingBindings()
        }

        binding.linearLayoutDob.singleClick {
            var datepicker = DatePickerDialog(
                activity!!, R.style.datepicker, dateSetListener,
                1998,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -80)
            datepicker.datePicker.minDate = calendar.timeInMillis
            datepicker.show()
        }

        binding.birthDate.singleClick {
            var datepicker = DatePickerDialog(
                activity!!, R.style.datepicker, dateSetListener,
                1998,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -80)
            datepicker.datePicker.minDate = calendar.timeInMillis
            datepicker.show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
