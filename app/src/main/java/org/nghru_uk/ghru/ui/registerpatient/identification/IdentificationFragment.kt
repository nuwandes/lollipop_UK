package org.nghru_uk.ghru.ui.registerpatient.identification

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import org.nghru_uk.ghru.databinding.IdentificationFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BitmapRxBus
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.SavedBitMap
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.UserConfig
import org.nghru_uk.ghru.vo.request.ParticipantMeta
import javax.inject.Inject

class IdentificationFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<IdentificationFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val disposables = CompositeDisposable()

    //private var savedBitmap: SavedBitMap? = null

    lateinit var participantMeta: ParticipantMeta

    //private var concentPhoto: String? = null
    //private var concentPhotoNew: String? = null

    private var nhsNumber: String? = null
    private var updatedNhsNumber: String? = null


    var userConfig: UserConfig? = null


    @Inject
    lateinit var viewModel: IdentificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //concentPhoto = arguments?.getString("concentPhotoPath")!!
            //concentPhotoNew = arguments?.getString("concentPhotoPathNew")!!
            participantMeta = arguments?.getParcelable<ParticipantMeta>("participantMeta")!!
            nhsNumber = arguments?.getString("NHS_number")!!

            userConfig = UserConfig.getUserConfig(participantMeta?.countryCode)

        } catch (e: KotlinNullPointerException) {

        }
//        disposables.add(
//            BitmapRxBus.getInstance().toObservable()
//                .subscribe({ result ->
//                    Log.d("Result", "Member ${result}")
//                    savedBitmap = result
//                    Log.d("Saved path", result.bitmapPath)
//                }, { error ->
//                    error.printStackTrace()
//                })
//        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<IdentificationFragmentBinding>(
            inflater,
            R.layout.identification_fragment,
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
        binding.root.hideKeyboard()
        binding.nextButton.singleClick {
//            if (validateNextButton()) {

                if (binding.textInputEditTextIdNumber.text.toString().equals(""))
                {
                    updatedNhsNumber = "NA"

                    participantMeta.body.idNumber =updatedNhsNumber
                    navController().navigate(
                        R.id.action_global_reviewFragment,
                        bundleOf("participantMeta" to participantMeta)
                    )
                }
                else if (binding.textInputEditTextIdNumber.text.toString().length != 10)
                {
                    Toast.makeText(activity!!, "Invalid NHS ID", Toast.LENGTH_LONG).show()
                }
                else
                {
                    updatedNhsNumber = binding.textInputEditTextIdNumber.text.toString()

                    participantMeta.body.idNumber =updatedNhsNumber
                    navController().navigate(
                        R.id.action_global_reviewFragment,
                        bundleOf("participantMeta" to participantMeta)
                    )
                }



//                if (binding.textInputEditTextIdNumber.text.toString().equals(""))
//                {
//                    updatedNhsNumber = "NA"
//
//                    Toast.makeText(activity!!, "Invalid NHS ID", Toast.LENGTH_LONG).show()
//                }
//                else
//                {
//                    binding.root.hideKeyboard()
//                    //participantMeta.body.idNumber =binding.textInputEditTextIdNumber.text.toString()
//                participantMeta.body.idNumber =nhsNumber
//                    //viewModel.setIdNumber(idNumber = participantMeta.body.idNumber)
//                    navController().navigate(
//                        R.id.action_global_reviewFragment,
////                        bundleOf("participantMeta" to participantMeta, "concentPhotoPath" to concentPhoto, "concentPhotoPathNew" to concentPhotoNew)
////                        bundleOf("participantMeta" to participantMeta, "concentPhotoPath" to concentPhoto)
//                        bundleOf("participantMeta" to participantMeta)
//                    )
//                }


//            }
        }

        viewModel.participant?.observe(this, Observer { participantResource ->
            if (participantResource?.status == Status.SUCCESS) {
                if(participantResource.data==null) {

                }else{
                    val errorDialogFragment = ErrorDialogFragment()
                    errorDialogFragment.setErrorMessage("The Paticipant ID already available")
                    errorDialogFragment.show(fragmentManager!!)
                }
            } else if (participantResource?.status == Status.ERROR) {
//                navController().navigate(
//                    R.id.action_global_reviewFragment,
//                    bundleOf("participantMeta" to participantMeta, "concentPhotoPath" to concentPhoto, "concentPhotoPathNew" to concentPhotoNew)
//                )
                //Crashlytics.logException(Exception(participantResource.toString()))
            }
            binding.executePendingBindings()
        })


//        if (savedBitmap != null) {
//
//            participantMeta.body.identityImage = savedBitmap?.bitmapPath!!
//            validateNextButton()
//
//            val rotationDegrees: Float? = savedBitmap?.bitmap?.rotationDegrees?.toFloat()
//            binding.userprofile.setRotation(-rotationDegrees!!);binding.userprofile.setImageBitmap(savedBitmap?.bitmap?.bitmap)
//            binding.cameraButton.visibility = View.INVISIBLE
//            binding.profileView.visibility = View.VISIBLE
//            binding.executePendingBindings()
//
//        } else {
//            binding.profileView.visibility = View.INVISIBLE
//            binding.cameraButton.visibility = View.VISIBLE
//        }

        binding.cameraButton.singleClick {
            binding.root.hideKeyboard()
            navController().navigate(R.id.action_global_cameraFragment)
        }

        binding.retakeBtn.singleClick {
            binding.root.hideKeyboard()
            //savedBitmap?.bitmapPath = ""
            participantMeta.body.identityImage = ""
            binding.userprofile.setImageBitmap(null)
            binding.cameraButton.visibility = View.VISIBLE
            binding.profileView.visibility = View.INVISIBLE
            //validateNextButton()

        }

        var watcher: TextWatcher = object : TextWatcherAdapter() {

            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                if (s.toString().length > 0) {
                    //validateNextButton()
                }
            }
        }

        if (nhsNumber != null)
        {
            binding.textInputEditTextIdNumber.setText(nhsNumber)
        }

        binding.textInputEditTextIdNumber.addTextChangedListener(watcher)

        //binding.textInputEditTextIdNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(userConfig?.nicMaxLength!!))

//        if(participantMeta?.countryCode.toString().toLowerCase().equals("uk"))
//        {
////            if (nhsNumber == null || nhsNumber.equals("")|| binding.textInputEditTextIdNumber.text.toString().equals(""))
////            {
////                Toast.makeText(activity!!, "NHS ID can not be blank", Toast.LENGTH_LONG).show()
////            }
//            if (nhsNumber == null || nhsNumber.equals(""))
//            {
//                Toast.makeText(activity!!, "NHS ID can not be blank", Toast.LENGTH_LONG).show()
//            }
//            else
//            {
//                binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
//                binding.nextButton.setDrawableRightColor("#0A1D53")
//                binding.nextButton.isEnabled = true
//                binding.NIDTextLayout.error = null
//            }
//        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateNextButton(): Boolean {


        if(participantMeta?.countryCode.toString().toLowerCase().equals("uk"))
        {
            binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
            binding.nextButton.setDrawableRightColor("#0A1D53")
            binding.nextButton.isEnabled = true
            binding.NIDTextLayout.error = null
            return true
        }
        return false
//        if ((!binding.textInputEditTextIdNumber.text.toString().equals(""))
//        if (nhsNumber != null)
//        {
//            binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
//            binding.nextButton.setDrawableRightColor("#0A1D53")
//            binding.nextButton.isEnabled = true
//            binding.NIDTextLayout.error = null
//            return true
//        } else {
//            binding.nextButton.setTextColor(Color.parseColor("#AED6F1"));
//            binding.nextButton.setDrawableRightColor("#AED6F1")
//            binding.nextButton.isEnabled = false
//            binding.NIDTextLayout.error = getString(R.string.nid_incorrect_1)
//            return false
//        }

    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
