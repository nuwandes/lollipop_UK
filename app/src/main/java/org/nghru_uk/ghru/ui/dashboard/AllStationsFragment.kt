package org.nghru_uk.ghru.ui.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.birbit.android.jobqueue.JobManager
import org.nghru_uk.ghru.*
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.AllStationsFragmentBinding
import org.nghru_uk.ghru.databinding.HomeFragmentBinding
import org.nghru_uk.ghru.di.Injectable
//import org.singapore.ghru.network.ConnectivityReceiver
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.setTitleColor
import org.nghru_uk.ghru.HLQSelfActivity
import timber.log.Timber
import javax.inject.Inject


class AllStationsFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<AllStationsFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var homeViewModel: AllStationsViewModel

    private var adapter by autoCleared<AllStationsAdapter>()

    @Inject
    lateinit var  jobManager: JobManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<AllStationsFragmentBinding>(
            inflater,
            R.layout.all_stations_fragment,
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_setting -> {
//                val intent = Intent(activity, SettingActivity::class.java)
//                startActivity(intent)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        jobManager.stop()
        binding.homeViewModel = homeViewModel

        val adapter = AllStationsAdapter(dataBindingComponent, appExecutors) { homeItem ->

            Timber.d(homeItem.toString())

            if (homeItem.id == 0)
            {
                val bundle = Bundle()
                bundle.putString("Station", "measurements")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 1)
            {
                val bundle = Bundle()
                bundle.putString("Station", "blood-pressure")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 2) {
                val bundle = Bundle()
                bundle.putString("Station", "sample")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 3) {
                val bundle = Bundle()
                bundle.putString("Station", "ecg")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 4) {
                val bundle = Bundle()
                bundle.putString("Station", "spirometry")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 5) {
                val bundle = Bundle()
                bundle.putString("Station", "fundoscopy")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 6)
            {
                val bundle = Bundle()
                bundle.putString("Station", "axivity")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
//            if (homeItem.id == 7)
//            {
//                val bundle = Bundle()
//                bundle.putString("Station", "staff-hlq")
//                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
//            }
            if (homeItem.id == 8)
            {
                val bundle = Bundle()
                bundle.putString("Station", "participant-hlq")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 9)
            {
                val bundle = Bundle()
                bundle.putString("Station", "intake")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
            if (homeItem.id == 10)
            {
                val bundle = Bundle()
                bundle.putString("Station", "checkout")
                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
            }
//            if (homeItem.id == 11)
//            {
//                val bundle = Bundle()
//                bundle.putString("Station", "reports")
//                navController().navigate(R.id.action_allStationFragment_to_selectedStationFragment, bundle)
//            }

        }
        this.adapter = adapter
        binding.allStationList.adapter = adapter
        binding.allStationList.setLayoutManager(GridLayoutManager(activity, 1))
        homeViewModel.setId("en")

        homeViewModel.homeItem.observe(this, Observer { listResource ->

            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        inflater?.inflate(R.menu.menu_main, menu)
//        //checkConnection(menu!!)
//    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()


    // Method to manually check connection status
//    private fun checkConnection(menu: Menu) {
//        val isConnected = ConnectivityReceiver.isConnected(context)
//        if (isConnected) {
//            menu.findItem(R.id.menu_text).setTitleColor(Color.WHITE)
//            menu.findItem(R.id.menu_text).setTitle("Online (Local)")
//            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_local_lan)
//        } else {
//            menu.findItem(R.id.menu_text).setTitleColor(Color.RED)
//            menu.findItem(R.id.menu_text).setTitle("Offline")
//            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_wifi_disconnected)
//        }
//        activity!!.invalidateOptionsMenu();
//    }
}
