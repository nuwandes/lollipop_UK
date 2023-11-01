package org.nghru_uk.ghru.ui.consent.upload

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BitmapRxBus
import org.nghru_uk.ghru.event.BitmapRxBusNew
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.hideKeyboard
import org.nghru_uk.ghru.util.showToast
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.SavedBitMap
import org.nghru_uk.ghru.vo.Status
import javax.inject.Inject
import androidx.lifecycle.Observer
import org.nghru_uk.ghru.databinding.UploadConsentFragmentBinding
import org.nghru_uk.ghru.ui.registerpatient.consent.uploadcopleted.UploadCompletedDialogFragment
import org.nghru_uk.ghru.vo.request.ParticipantRequest


class UploadConsentFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<UploadConsentFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)


    //private var member: Member? = null

    //private var householdId: String? = null

    private var add_info: String? = null

    //var meta: Meta? = null

    //var hoursFasted: String? = null

    //var household: HouseholdRequest? = null

    private val disposables = CompositeDisposable()

    private var savedBitmap: SavedBitMap? = null
    private var savedBitmap1: SavedBitMap? = null
    private var participant: String? = null
    private var consentCount: Int? = null

    @Inject
    lateinit var confirmationViewModel: UploadConsentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getString("Participant")!!
            consentCount = arguments?.getInt("ConsentCount")
        } catch (e: KotlinNullPointerException) {

        }
        try {
        } catch (e: KotlinNullPointerException) {

        }

        disposables.add(
            BitmapRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "Member ${result}")
                    savedBitmap = result
                    Log.d("Saved path", result.bitmapPath)
                }, { error ->
                    error.printStackTrace()
                })
        )

        disposables.add(
            BitmapRxBusNew.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "Member ${result}")
                    savedBitmap1 = result
                    Log.d("Saved path", result.bitmapPath)
                }, { error ->
                    error.printStackTrace()
                })
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<UploadConsentFragmentBinding>(
            inflater,
            R.layout.upload_consent_fragment,
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

        if (consentCount == 0)
        {
            binding.FirstConsentForm.visibility = View.VISIBLE
            binding.secondConsentForm.visibility = View.VISIBLE
        }
        else
        {
            binding.FirstConsentForm.visibility = View.VISIBLE
        }

        binding.buttonAcceptAndContinue.singleClick {

            if (isNetworkAvailable())
            {
                if (savedBitmap != null)
                {
                    binding.progressBar.visibility = View.VISIBLE
                    confirmationViewModel.setUploadConcent(savedBitmap?.bitmapPath, participant!!)
                }
                else
                {
                    activity!!.showToast(getString(R.string.please_take_image))
                }
            }
            else
            {
                activity!!.showToast(getString(R.string.no_network_connection_sg))
            }
        }

        confirmationViewModel.uploadConcent?.observe(this, Observer { upload ->

            if (upload?.status == Status.SUCCESS) {
                Crashlytics.logException(Exception("uploadConcent sucess"))
                Log.d("REGISTRATION_CONFIRM", "FIRST UPLOAD SUCCESS: ")

                if (savedBitmap1 != null)
                {
                    confirmationViewModel.setUploadConsentNew(savedBitmap1?.bitmapPath, participant!!)
                }
                else
                {
                    val completedDialogFragment = UploadCompletedDialogFragment()
                    completedDialogFragment.arguments = bundleOf("message" to getString(R.string.string_upload_complete))
                    completedDialogFragment.show(fragmentManager!!)

                    binding.progressBar.visibility = View.GONE
                }

            }
            else if (upload?.status == Status.ERROR)
            {
                Log.d("REGISTRATION_CONFIRM", "FIRST UPLOAD FAILED: ")
            }
        })

        confirmationViewModel.uploadConsentNew?.observe(this, Observer { upload ->

            if (upload?.status == Status.SUCCESS) {
                Crashlytics.logException(Exception("uploadConcent sucess"))
                Log.d("REGISTRATION_CONFIRM", "SECOND SUCCESS: ")

                val completedDialogFragment = UploadCompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("message" to getString(R.string.string_upload_complete_1))
                completedDialogFragment.show(fragmentManager!!)

                binding.progressBar.visibility = View.GONE

            } else if (upload?.status == Status.ERROR) {

                Log.d("REGISTRATION_CONFIRM", "SECOND UPLOAD FAILED: ")
            }
        })

//        binding.saveAndExitButton.singleClick {
//            val mDeleteFragmentDialog = ExplanationDialogFragment()
//            mDeleteFragmentDialog.show(fragmentManager!!)
//        }


        if (savedBitmap != null) {
            val rotationDegrees: Float? = savedBitmap?.bitmap?.rotationDegrees?.toFloat()
            binding.userprofile.setRotation(-rotationDegrees!!);
            binding.userprofile.setImageBitmap(savedBitmap?.bitmap?.bitmap)
            binding.cameraButton.visibility = View.INVISIBLE
            binding.profileView.visibility = View.VISIBLE
            binding.executePendingBindings()
        } else {
            binding.profileView.visibility = View.INVISIBLE
            binding.cameraButton.visibility = View.VISIBLE
        }

        binding.cameraButton.singleClick {
            binding.root.hideKeyboard()
            navController().navigate(R.id.action_global_cameraFragment)

        }

        binding.retakeBtn.singleClick {
            binding.root.hideKeyboard()
            savedBitmap?.bitmapPath = ""
            savedBitmap = null
            binding.userprofile.setImageBitmap(null)
            binding.cameraButton.visibility = View.VISIBLE
            binding.profileView.visibility = View.INVISIBLE
            // validateNextButton()

        }

//        second concent form --------------------------------------------

        if (savedBitmap1 != null) {

            val rotationDegrees1: Float? = savedBitmap1?.bitmap?.rotationDegrees?.toFloat()
            binding.userprofile1.setRotation(-rotationDegrees1!!)

//            val rotationDegrees: Float? = savedBitmap1?.bitmap?.rotationDegrees?.toFloat()
//            binding.userprofile1.setRotation(-rotationDegrees!!)

            binding.userprofile1.setImageBitmap(savedBitmap1?.bitmap?.bitmap)
            binding.cameraButton1.visibility = View.INVISIBLE
            binding.profileView1.visibility = View.VISIBLE
            binding.executePendingBindings()
        } else {

            binding.profileView1.visibility = View.INVISIBLE
            binding.cameraButton1.visibility = View.VISIBLE

//            binding.profileView.visibility = View.INVISIBLE
//            binding.cameraButton.visibility = View.VISIBLE

        }

        binding.cameraButton1.singleClick {
            binding.root.hideKeyboard()

            navController().navigate(R.id.action_global_cameraFragmentNew)

            //navController().navigate(R.id.action_global_cameraFragment)


        }

        binding.retakeBtn1.singleClick {
            binding.root.hideKeyboard()
            savedBitmap1?.bitmapPath = ""
            savedBitmap1 = null
            binding.userprofile1.setImageBitmap(null)
            binding.cameraButton1.visibility = View.VISIBLE
            binding.profileView1.visibility = View.INVISIBLE

        }

        // ---------------------------------------------------------------

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            android.R.id.home -> {
//                return navController().navigateUp()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
