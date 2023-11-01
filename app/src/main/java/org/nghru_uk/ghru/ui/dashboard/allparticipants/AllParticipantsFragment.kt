package org.nghru_uk.ghru.ui.dashboard.allparticipants

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import org.nghru_uk.ghru.databinding.AllParticipantsFragmentBinding
import org.nghru_uk.ghru.databinding.AllStationsFragmentBinding
import org.nghru_uk.ghru.databinding.HomeFragmentBinding
import org.nghru_uk.ghru.di.Injectable
//import org.singapore.ghru.network.ConnectivityReceiver
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.setTitleColor
import org.nghru_uk.ghru.HLQSelfActivity
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.*
import timber.log.Timber
import javax.inject.Inject


class AllParticipantsFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<AllParticipantsFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var homeViewModel: AllParticipantsViewModel

    private var adapter by autoCleared<AllParticipantsAdapter>()

    @Inject
    lateinit var  jobManager: JobManager

    private var participantListObject: List<ParticipantStationItemNewNew?> = arrayListOf()

    private var searchKey: String? = ""

    private var participantListMeta: ParticipantListMeta? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<AllParticipantsFragmentBinding>(
            inflater,
            R.layout.all_participants_fragment,
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

        binding.progressBar.visibility = View.VISIBLE

        val adapter = AllParticipantsAdapter(dataBindingComponent, appExecutors, activity!!) { homeItem ->

            Timber.d(homeItem.toString())
        }
        this.adapter = adapter
        binding.allParticipantsList.adapter = adapter
        binding.allParticipantsList.setLayoutManager(GridLayoutManager(activity, 1))
        homeViewModel.setId("en")

        homeViewModel.setFilterId(1, searchKey!!)

        homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

            if (isNetworkAvailable())
            {
                if (it.status.equals(Status.SUCCESS))
                {
                    val stationList: ArrayList<ParticipantStationItemNewNew> = ArrayList<ParticipantStationItemNewNew>()

                    it.data!!.data!!.forEach{parti ->
                        stationList.add(parti)
                    }

                    participantListObject = stationList
                    adapter.submitList(participantListObject)
                    participantListMeta = it.data.meta
                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                    binding.progressBar.visibility = View.GONE
                }
                else
                {
                    adapter.submitList(emptyList())
                    binding.progressBar.visibility = View.GONE
                    //binding.participantProgressBar.visibility = View.GONE
                }
            }
            else
            {
                Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
            }

        })

        binding.textInputEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //println(s)
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 0)
                {
                    searchKey = s.toString()

                } else {
                    homeViewModel.setId("en")
                }
            }
        })

        binding.firstButton.singleClick {

            if (participantListMeta != null)
            {
                binding.progressBar.visibility = View.VISIBLE
                val lastPage = participantListMeta!!.last_page!!.toInt()

                val firstPage = lastPage - (lastPage-1)

                homeViewModel.setFilterId(firstPage, searchKey!!)

                homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (isNetworkAvailable())
                    {
                        if (it.status.equals(Status.SUCCESS))
                        {
                            val stationList: ArrayList<ParticipantStationItemNewNew> = ArrayList<ParticipantStationItemNewNew>()

                            it.data!!.data!!.forEach{parti ->
                                stationList.add(parti)
                            }

                            participantListObject = stationList
                            adapter.submitList(participantListObject)
                            participantListMeta = it.data.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            binding.progressBar.visibility = View.GONE
                        }
                        else
                        {
                            adapter.submitList(emptyList())
                            binding.progressBar.visibility = View.GONE
                            //binding.participantProgressBar.visibility = View.GONE
                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                    }

                })
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.previousButton.singleClick {

            if (participantListMeta != null)
            {
                binding.progressBar.visibility = View.VISIBLE
                val prPageNumber = participantListMeta!!.current_page!!.toInt() - 1

                homeViewModel.setFilterId(prPageNumber, searchKey!!)

                homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (isNetworkAvailable())
                    {
                        if (it.status.equals(Status.SUCCESS))
                        {
                            val stationList: ArrayList<ParticipantStationItemNewNew> = ArrayList<ParticipantStationItemNewNew>()

                            it.data!!.data!!.forEach{parti ->
                                stationList.add(parti)
                            }

                            participantListObject = stationList
                            adapter.submitList(participantListObject)
                            participantListMeta = it.data.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            binding.progressBar.visibility = View.GONE
                        }
                        else
                        {
                            adapter.submitList(emptyList())
                            binding.progressBar.visibility = View.GONE
                            //binding.participantProgressBar.visibility = View.GONE
                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                    }

                })
            }
            else
            {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
            }
        }

        binding.nextButton.singleClick {

            if (participantListMeta != null)
            {
                if (participantListMeta!!.current_page!!.toInt() != participantListMeta!!.last_page!!.toInt())
                {
                    binding.progressBar.visibility = View.VISIBLE
                    val nextPageNumber = participantListMeta!!.current_page!!.toInt() + 1

                    homeViewModel.setFilterId(nextPageNumber, searchKey!!)

                    homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                        if (isNetworkAvailable())
                        {
                            if (it.status.equals(Status.SUCCESS))
                            {
                                val stationList: ArrayList<ParticipantStationItemNewNew> = ArrayList<ParticipantStationItemNewNew>()

                                it.data!!.data!!.forEach{parti ->
                                    stationList.add(parti)
                                }

                                participantListObject = stationList
                                adapter.submitList(participantListObject)
                                participantListMeta = it.data.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                binding.progressBar.visibility = View.GONE
                            }
                            else
                            {
                                adapter.submitList(emptyList())
                                binding.progressBar.visibility = View.VISIBLE
                                //binding.participantProgressBar.visibility = View.GONE
                            }
                        }
                        else
                        {
                            Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                        }

                    })

                }
                else
                {
                    Toast.makeText(activity, "Data already reached the last page", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }


        }

        binding.lastButton.singleClick {

            if (participantListMeta != null)
            {
                binding.progressBar.visibility = View.VISIBLE
                val lastPage = participantListMeta!!.last_page!!.toInt()

                homeViewModel.setFilterId(lastPage, searchKey!!)

                homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (isNetworkAvailable())
                    {
                        if (it.status.equals(Status.SUCCESS))
                        {
                            val stationList: ArrayList<ParticipantStationItemNewNew> = ArrayList<ParticipantStationItemNewNew>()

                            it.data!!.data!!.forEach{parti ->
                                stationList.add(parti)
                            }

                            participantListObject = stationList
                            adapter.submitList(participantListObject)
                            participantListMeta = it.data.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            binding.progressBar.visibility = View.GONE
                        }
                        else
                        {
                            adapter.submitList(emptyList())
                            binding.progressBar.visibility = View.GONE
                            //binding.participantProgressBar.visibility = View.GONE
                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                    }
                })

            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.searchButton.singleClick {

            adapter.submitList(emptyList())

            val adapter1 = AllParticipantsAdapter(dataBindingComponent, appExecutors, activity!!) { homeItem ->

                Timber.d(homeItem.toString())
            }

            adapter1.submitList(emptyList())

            binding.progressBar.visibility = View.VISIBLE

            var participantListObject: List<ParticipantStationItemNewNew?> = arrayListOf()
            val stationList: ArrayList<ParticipantStationItemNewNew> = ArrayList<ParticipantStationItemNewNew>()
            Log.d("ALL_PARTICIPANT_FRAG", "LIST_OBJECT_BEFORE_MODEL: " + participantListObject.size)

            homeViewModel.setFilterId(page = 1, keyWord = searchKey!!)

            homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                if (isNetworkAvailable())
                {
                    if (it.status.equals(Status.SUCCESS))
                    {
                        it.data!!.data!!.forEach{parti ->
                            stationList.add(parti)
                        }

                        participantListObject = stationList
                        adapter1.submitList(participantListObject)
                        binding.progressBar.visibility = View.GONE
                        //Log.d("ALL_PARTICIPANT_FRAG", "LIST_OBJECT_INSIDE_MODEL: " + participantListObject.get(0)?.listRequest?.get(0))

                    }
                    else
                    {
                        adapter1.submitList(emptyList())
                        binding.progressBar.visibility = View.GONE
                        //adapter1.notifyDataSetChanged()
                        //binding.participantProgressBar.visibility = View.GONE
                    }


                }
                else
                {
                    Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                }
            })

            adapter1.notifyDataSetChanged()
        }

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

}
