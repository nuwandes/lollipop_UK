package org.nghru_uk.ghru.ui.enumeration.concent

import android.os.Bundle
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.ConcentFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.ui.enumeration.concent.reasondialog.ReasonDialogFragment
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.request.Consent
import org.nghru_uk.ghru.vo.request.HouseholdRequest
import javax.inject.Inject


class ConcentFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ConcentFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var concentViewModel: ConcentViewModel

    private lateinit var household: HouseholdRequest

    private lateinit var consent: Consent
    private lateinit var meta: Meta
    var countryCode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            household = arguments?.getParcelable<HouseholdRequest>("HouseholdRequest")!!
            meta = arguments?.getParcelable<Meta>("meta")!!
            countryCode = arguments?.getString("countryCode")
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ConcentFragmentBinding>(
            inflater,
            R.layout.concent_fragment,
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
            //  household.consent = true
            consent = Consent(status = true, reason = null)
            household.consent = consent
            val bundle = bundleOf("HouseholdRequest" to household, "meta" to meta, "countryCode" to countryCode!!.trim())
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_concentFragment_to_addHouseHoldMember, bundle)

        }

        binding.saveAndExitButton.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.household = household
            reasonDialogFragment.meta = meta
            reasonDialogFragment.show(fragmentManager!!)
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


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
