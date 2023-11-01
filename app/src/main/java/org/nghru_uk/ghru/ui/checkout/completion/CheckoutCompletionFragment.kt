package org.nghru_uk.ghru.ui.checkout.completion

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
import org.nghru_uk.ghru.databinding.CheckoutCompletionFragmentBinding
import org.nghru_uk.ghru.databinding.ManualEntryBodyMeasurementFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.event.BusProvider
import org.nghru_uk.ghru.event.StationCheckRxBus
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.AlreadyCheckoutDialogFragment
//import org.nghru_uk.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment
//import org.nghru_uk.ghru.ui.statuscheck.StatusCheckDialogFragment
import org.nghru_uk.ghru.util.*
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CheckoutCompletionFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<CheckoutCompletionFragmentBinding>()

    @Inject
    lateinit var viewModel: CheckoutCompletionViewModel

    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    var meta: Meta? = null

    var status_code: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //meta = arguments?.getParcelable<Meta>("meta")!!
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<CheckoutCompletionFragmentBinding>(
            inflater,
            R.layout.checkout_completion_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        //appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (participantRequest != null)
        {
            binding.participantId.setText(participantRequest!!.screeningId)
        }

        binding.bankButton.singleClick {

//            val bundle = bundleOf("ParticipantRequest" to participantRequest)
//            findNavController().navigate(R.id.action_checkoutCompletionFragment_to_BankDetailsFragment, bundle)

            activity!!.finish()
        }

//        binding.voucherButton.singleClick {
//
//            val bundle = bundleOf("ParticipantRequest" to participantRequest)
//            findNavController().navigate(R.id.action_selectedCompletionFragment_to_VoucherDetailsFragment, bundle)
//        }
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

    fun navController() = findNavController()

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }
}