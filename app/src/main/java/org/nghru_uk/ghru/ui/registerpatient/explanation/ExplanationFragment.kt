package org.nghru_uk.ghru.ui.registerpatient.explanation

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
import io.reactivex.disposables.CompositeDisposable
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.ExplanationFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BitmapRxBus
import org.nghru_uk.ghru.event.BitmapRxBusNew
import org.nghru_uk.ghru.ui.registerpatient.explanation.reasondialog.ExplanationDialogFragment
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.hideKeyboard
import org.nghru_uk.ghru.util.showToast
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.SavedBitMap
import org.nghru_uk.ghru.vo.request.HouseholdRequest
import org.nghru_uk.ghru.vo.request.Member
import javax.inject.Inject


class ExplanationFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<ExplanationFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)


    private var member: Member? = null

    private var householdId: String? = null

    private var add_info: String? = null

    var meta: Meta? = null

    //var hoursFasted: String? = null

    var household: HouseholdRequest? = null

    private val disposables = CompositeDisposable()

    //private var savedBitmap: SavedBitMap? = null

    //private var savedBitmap1: SavedBitMap? = null

    //private var savedBitmap1: SavedBitMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            member = arguments?.getParcelable<Member>("member")!!
        } catch (e: KotlinNullPointerException) {

        }
        try {
            householdId = arguments?.getString("householdId")!!
            meta = arguments?.getParcelable<Meta>("meta")!!
            //hoursFasted = arguments?.getString("hours_fasted")
            household = arguments?.getParcelable("household")
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

//        disposables.add(
//            BitmapRxBusNew.getInstance().toObservable()
//                .subscribe({ result ->
//                    Log.d("Result", "Member ${result}")
//                    savedBitmap1 = result
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
        val dataBinding = DataBindingUtil.inflate<ExplanationFragmentBinding>(
            inflater,
            R.layout.explanation_fragment,
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



        binding.buttonAcceptAndContinue.singleClick {
//            if (savedBitmap != null && savedBitmap1 != null) {
//            if (savedBitmap != null) {

//                navController().navigate(
//                    R.id.action_global_BasicDetailsFragment,
//                    bundleOf(
//                        "member" to member,
//                        "householdId" to householdId,
//                        "hours_fasted" to hoursFasted,
//                        "meta" to meta,
//                        "household" to household,
//                        "concentPhotoPath" to savedBitmap?.bitmapPath
//                    )
//                )

//                navController().navigate(
//                    R.id.action_global_BasicDetailsFragment,
//                    bundleOf(
//                        "member" to member,
//                        "householdId" to householdId,
//                        "meta" to meta,
//                        "household" to household,
//                        "concentPhotoPath" to savedBitmap?.bitmapPath
//                    )
//                )

//            } else {
//                activity!!.showToast(getString(R.string.please_take_image))
//            }

            navController().navigate(
                R.id.action_global_BasicDetailsFragment,
                bundleOf(
                    "member" to member,
                    "householdId" to householdId,
                    "meta" to meta,
                    "household" to household
                )
            )
        }

        binding.saveAndExitButton.singleClick {
            val mDeleteFragmentDialog = ExplanationDialogFragment()
            mDeleteFragmentDialog.show(fragmentManager!!)
        }


//        if (savedBitmap != null) {
//            val rotationDegrees: Float? = savedBitmap?.bitmap?.rotationDegrees?.toFloat()
//            binding.userprofile.setRotation(-rotationDegrees!!);
//            binding.userprofile.setImageBitmap(savedBitmap?.bitmap?.bitmap)
//            binding.cameraButton.visibility = View.INVISIBLE
//            binding.profileView.visibility = View.VISIBLE
//            binding.executePendingBindings()
//        } else {
//            binding.profileView.visibility = View.INVISIBLE
//            binding.cameraButton.visibility = View.VISIBLE
//        }
//
//        binding.cameraButton.singleClick {
//            binding.root.hideKeyboard()
//            navController().navigate(R.id.action_global_cameraFragment)
//
//        }
//
//        binding.retakeBtn.singleClick {
//            binding.root.hideKeyboard()
//            savedBitmap?.bitmapPath = ""
//            // participantMeta.body.identityImage = ""
//            binding.userprofile.setImageBitmap(null)
//            binding.cameraButton.visibility = View.VISIBLE
//            binding.profileView.visibility = View.INVISIBLE
//            // validateNextButton()
//
//        }

//        second concent form --------------------------------------------

//        if (savedBitmap1 != null) {

//            val rotationDegrees1: Float? = savedBitmap1?.bitmap?.rotationDegrees?.toFloat()
//            binding.userprofile1.setRotation(-rotationDegrees1!!)

//            val rotationDegrees: Float? = savedBitmap1?.bitmap?.rotationDegrees?.toFloat()
//            binding.userprofile1.setRotation(-rotationDegrees!!)

//            binding.userprofile1.setImageBitmap(savedBitmap1?.bitmap?.bitmap)
//            binding.cameraButton1.visibility = View.INVISIBLE
//            binding.profileView1.visibility = View.VISIBLE
//            binding.executePendingBindings()
//        } else {

//            binding.profileView1.visibility = View.INVISIBLE
//            binding.cameraButton1.visibility = View.VISIBLE

//            binding.profileView.visibility = View.INVISIBLE
//            binding.cameraButton.visibility = View.VISIBLE

//        }
//
//        binding.cameraButton1.singleClick {
//            binding.root.hideKeyboard()

//            navController().navigate(R.id.action_global_cameraFragmentNew)

//            navController().navigate(R.id.action_global_cameraFragment)

//
//        }
//
//        binding.retakeBtn1.singleClick {
//            binding.root.hideKeyboard()
//            savedBitmap1?.bitmapPath = ""
//            binding.userprofile1.setImageBitmap(null)
//            binding.cameraButton1.visibility = View.VISIBLE
//            binding.profileView1.visibility = View.INVISIBLE
//
//        }

        // ---------------------------------------------------------------

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
