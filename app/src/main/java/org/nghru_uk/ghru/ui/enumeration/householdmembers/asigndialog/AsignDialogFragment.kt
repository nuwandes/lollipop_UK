package org.nghru_uk.ghru.ui.enumeration.householdmembers.asigndialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.AsignDialogFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.util.LocaleManager
import org.nghru_uk.ghru.util.TokenManager
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.request.HouseholdRequest
import org.nghru_uk.ghru.vo.request.Member
import java.util.*
import javax.inject.Inject

class AsignDialogFragment : DialogFragment(), Injectable {

    val TAG = AsignDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager

    var binding by autoCleared<AsignDialogFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var jobManager: JobManager

    private var meta: Meta? = null

    private var memberList: ArrayList<Member>? = null
    var countryCode: String? = ""

    private var household: HouseholdRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            meta = arguments?.getParcelable<Meta>("meta")!!
            household = arguments?.getParcelable<HouseholdRequest>("HouseholdRequest")
            memberList = arguments?.getParcelableArrayList<Member>("memberList")
            countryCode = arguments?.getString("countryCode")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<AsignDialogFragmentBinding>(
            inflater,
            R.layout.asign_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        binding.buttonAddMember.singleClick {
            dismiss()
            val bundle = bundleOf("HouseholdRequest" to household, "meta" to meta, "countryCode" to countryCode)
            bundle.putBoolean("more", true)
            navController().navigate(R.id.action_global_addHouseHoldMember, bundle)
        }

        binding.buttonAcceptAndContinue.singleClick {
            dismiss()
            val bundle = bundleOf(
                "HouseholdRequest" to household,
                "memberList" to memberList,
                "meta" to meta,
                "countryCode" to countryCode
            )
            navController().navigate(R.id.action_householdMembersFragment_to_scanCodeFragment, bundle)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // creating the fullscreen dialog
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

}
