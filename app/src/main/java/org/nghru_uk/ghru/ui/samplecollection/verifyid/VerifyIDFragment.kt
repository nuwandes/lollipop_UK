package org.nghru_uk.ghru.ui.samplecollection.verifyid


import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.SamplecollectionVerifyIdFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.io.File
import javax.inject.Inject

class VerifyIDFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<SamplecollectionVerifyIdFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var verifyIDViewModel: VerifyIDViewModel

    private var participant: ParticipantRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<SamplecollectionVerifyIdFragmentBinding>(
            inflater,
            R.layout.samplecollection_verify_id_fragment,
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
        binding.setLifecycleOwner(this)
        // binding.viewModel = verifyIDViewModel
        binding.participant = participant
        binding.buttonSubmit.singleClick({
            var bundle = bundleOf("participant" to participant)
            navController().navigate(R.id.action_verifyIDFragment_to_fastedFragment, bundle)
        })

        if (isNetworkAvailable()) {
            Glide.with(this).load(participant?.profileImage).into(binding.profileImage)

        } else {
            try {
                val imgFile = File(participant?.profileImage)
                if (imgFile.exists()) {

                    val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                    binding.profileImage.setImageBitmap(myBitmap)

                }
            } catch (e: Exception) {

            }

        }

    }


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
