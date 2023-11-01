package org.nghru_uk.ghru.ui.dashboard.selectedstation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.NonNull
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
import org.nghru_uk.ghru.databinding.SelectedStationFragmentBinding
import org.nghru_uk.ghru.di.Injectable
//import org.singapore.ghru.network.ConnectivityReceiver
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.setTitleColor
import org.nghru_uk.ghru.HLQSelfActivity
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.*
import org.nghru_uk.ghru.vo.Date
import timber.log.Timber
import java.lang.Error
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

import javax.inject.Inject


class SelectedStationFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<SelectedStationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var homeViewModel: SelectedStationViewModel

    private var adapter by autoCleared<SelectedStationAdapter>()

    @Inject
    lateinit var  jobManager: JobManager

    private var selectedStation: String? = null
    private var station: String? = null
    private var participantIDList: MutableList<String> = arrayListOf()
    private var registeredDateList: MutableList<String> = arrayListOf()
    var cal = Calendar.getInstance()
    val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    var selectedDate : String? = null
    private var participantListObject: List<ParticipantListItem?> = arrayListOf()
    private var selectedStationId: String? = null
    var participantAdapter:ArrayAdapter<String>? = null
    private var selectedStatus:String? = null
    private var selectedStatusId:String? = null
    private var participantListMeta:ParticipantListMeta? = null
    private var statuses: MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            station = arguments?.getString("Station")
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<SelectedStationFragmentBinding>(
            inflater,
            R.layout.selected_station_fragment,
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

        if (station != null)
        {
            if (station == "measurements")
            {
                selectedStation = "measurements"
                binding.stationName.setText(R.string.screening_body_measurements)
                selectedStationId = "c6e20067-d106-4316-9c33-0f144b2a2632"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_body_measurements)
            }
            else if (station == "blood-pressure")
            {
                selectedStation = "blood-pressure"
                binding.stationName.setText(R.string.screening_blood_pressure)
                selectedStationId = "bd0bdb56-e193-11e8-9f32-f2801f1b9fd1"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_bp)
            }
            else if (station == "sample")
            {
                selectedStation = "sample"
                binding.stationName.setText(R.string.screening_biological_samples)
                selectedStationId = "603e9578-975c-4da6-b79e-1f407a7a677e"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_bio_samples)
            }
            else if (station == "ecg")
            {
                selectedStation = "ecg"
                binding.stationName.setText(R.string.ecg)
                selectedStationId = "b277deb1-2c8f-4b39-8897-0a6c4d7edab7"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_ecg)
            }
            else if (station == "spirometry")
            {
                selectedStation = "spirometry"
                binding.stationName.setText(R.string.spirometry)
                selectedStationId = "8a10bfef-2f3e-4961-bbb2-d1c57ce5b807"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_spirometry)
            }
            else if (station == "fundoscopy")
            {
                selectedStation = "fundoscopy"
                binding.stationName.setText(R.string.fundoscopy)
                selectedStationId = "8502ed98-1342-46f7-9f73-10d3d8c40895"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_fundoscopy)
            }
            else if (station == "axivity")
            {
                selectedStation = "axivity"
                binding.stationName.setText(R.string.activity_tracker)
                selectedStationId = "29fc1d84-957a-4608-9791-7d7523d653a6"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_activity_tracker)
            }
//            else if (station == "staff-hlq")
//            {
//                selectedStation = "staff-hlq"
//                binding.stationName.setText(R.string.screening_hlq_staff)
//                selectedStationId = "97632fcd-fe13-431f-9a06-5c8d8adb99d9"
//                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_healthy_lifestyle)
//            }
            else if (station == "participant-hlq")
            {
                selectedStation = "participant-hlq"
                binding.stationName.setText(R.string.screening_hlq_self)
                selectedStationId = "716cc0a2-d611-11ea-87d0-0242ac130003"
                binding.imageViewIcon.setImageResource(R.drawable.self1_36)
            }
            else if (station == "intake")
            {
                selectedStation = "intake"
                binding.stationName.setText(R.string.screening_intake24)
                selectedStationId = "29b452bf-9583-4a87-a785-1aab54cd0ce5"
                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_intake)
            }
            else if (station == "checkout")
            {
                selectedStation = "checkout"
                binding.stationName.setText(R.string.screening_checkout)
                selectedStationId = "39e6fcb8-6c67-11eb-9439-0242ac130002"
                binding.imageViewIcon.setImageResource(R.drawable.checkout_36)
            }
//            else if (station == "reports")
//            {
//                selectedStation = "reports"
//                binding.stationName.setText(R.string.home_reports)
//                selectedStationId = "c95a4380-b08d-4d73-a10c-c36a0230cdf2"
//                binding.imageViewIcon.setImageResource(R.drawable.ic_icon_medical_report)
//            }

        }

        val adapter = SelectedStationAdapter(dataBindingComponent, appExecutors) { homeItem ->

            Timber.d(homeItem.toString())
        }
        this.adapter = adapter
        binding.selectedStationList.adapter = adapter
        binding.selectedStationList.setLayoutManager(GridLayoutManager(activity, 1))
        homeViewModel.setId("en")

        participantIDList.clear()
        //participantIDList.add(getString(R.string.unknown_partcipant))
        participantIDList.add("Participant ID")
        participantIDList.add("Status")
        participantAdapter = ArrayAdapter(context!!, R.layout.station_spinner_dropdown_item, participantIDList)
        binding.participantIdSpinner.setAdapter(participantAdapter)

        statuses.clear()
        statuses.add(getString(R.string.status_default))
        statuses.add(getString(R.string.status_in_progress))
        statuses.add(getString(R.string.status_not_started))
        statuses.add(getString(R.string.status_completed))
        val adapter2 = ArrayAdapter(context!!, R.layout.station_spinner_dropdown_item, statuses)
        binding.statusSpinner.setAdapter(adapter2)

        binding.lastMealDate.setText(sdf.format(java.util.Date()).toString())

        binding.lastMealDate.singleClick {

            val dialog = datePickerDialog()
            dialog.show()

            dialog.datePicker.setCalendarViewShown(false)
            dialog.datePicker.setSpinnersShown(true)
            dialog.datePicker.maxDate = System.currentTimeMillis()

            binding.progressBar.visibility = View.VISIBLE
            binding.noRecordsView.visibility = View.GONE
        }

        homeViewModel.setFilterId(page = 1, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = "participant")

        homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

            if (isNetworkAvailable())
            {
                if (it.status.equals(Status.SUCCESS))
                {
                    val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                    it.data!!.data!!.listRequest!!.forEach{parti ->
                        partiList.add(parti!!)
                    }

                    if (partiList.size > 0)
                    {
                        participantListObject = partiList
                        adapter.submitList(participantListObject)
                        participantAdapter!!.notifyDataSetChanged()
                        participantListMeta = it.data.meta
                        binding.progressBar.visibility = View.GONE
                        binding.noRecordsView.visibility = View.GONE
                    }
                    else
                    {
                        binding.noRecordsView.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }
                }
                else
                {
                    adapter.submitList(emptyList())
                    //binding.progressBar.visibility = View.GONE
                }
            }
            else
            {
                Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
            }
        })

        binding.firstButton.singleClick {

            if (participantListMeta != null)
            {
                val lastPage = participantListMeta!!.last_page!!.toInt()

                val firstPage = lastPage - (lastPage-1)

                homeViewModel.setFilterId(page = firstPage, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

                homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (isNetworkAvailable())
                    {
                        if (it.status.equals(Status.SUCCESS))
                        {
                            val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                            it.data!!.data!!.listRequest!!.forEach{parti ->
                                partiList.add(parti!!)
                            }

                            if (partiList.size > 0)
                            {
                                participantListObject = partiList
                                adapter.submitList(participantListObject)
                                participantAdapter!!.notifyDataSetChanged()
                                participantListMeta = it.data.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                binding.progressBar.visibility = View.GONE
                                binding.noRecordsView.visibility = View.GONE
                            }
                            else
                            {
                                binding.noRecordsView.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                        else
                        {
                            adapter.submitList(emptyList())
                            //binding.progressBar.visibility = View.GONE
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
            }
        }

        binding.previousButton.singleClick {

            if (participantListMeta != null)
            {
                val prPageNumber = participantListMeta!!.current_page!!.toInt() - 1

                homeViewModel.setFilterId(page = prPageNumber, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

                homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (isNetworkAvailable())
                    {
                        if (it.status.equals(Status.SUCCESS))
                        {
                            val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                            it.data!!.data!!.listRequest!!.forEach{parti ->
                                partiList.add(parti!!)
                            }

                            if (partiList.size > 0)
                            {
                                participantListObject = partiList
                                adapter.submitList(participantListObject)
                                participantAdapter!!.notifyDataSetChanged()
                                participantListMeta = it.data.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                binding.progressBar.visibility = View.GONE
                                binding.noRecordsView.visibility = View.GONE
                            }
                            else
                            {
                                binding.noRecordsView.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                        else
                        {
                            adapter.submitList(emptyList())
                            //binding.progressBar.visibility = View.GONE
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
            }
        }

        binding.nextButton.singleClick {

            if (participantListMeta != null)
            {
                if (participantListMeta!!.current_page!!.toInt() != participantListMeta!!.last_page!!.toInt())
                {
                    val nextPageNumber = participantListMeta!!.current_page!!.toInt() + 1

                    homeViewModel.setFilterId(page = nextPageNumber, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

                    homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                        if (isNetworkAvailable())
                        {
                            if (it.status.equals(Status.SUCCESS))
                            {
                                val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                                it.data!!.data!!.listRequest!!.forEach{parti ->
                                    partiList.add(parti!!)
                                }

                                if (partiList.size > 0)
                                {
                                    participantListObject = partiList
                                    adapter.submitList(participantListObject)
                                    participantAdapter!!.notifyDataSetChanged()
                                    participantListMeta = it.data.meta
                                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                    binding.progressBar.visibility = View.GONE
                                    binding.noRecordsView.visibility = View.GONE
                                }
                                else
                                {
                                    binding.noRecordsView.visibility = View.VISIBLE
                                    binding.progressBar.visibility = View.GONE
                                }
                            }
                            else
                            {
                                adapter.submitList(emptyList())
                                //binding.progressBar.visibility = View.GONE
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
                }
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
            }


        }

        binding.lastButton.singleClick {

            if (participantListMeta != null)
            {
                val lastPage = participantListMeta!!.last_page!!.toInt()

                homeViewModel.setFilterId(page = lastPage, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

                homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (isNetworkAvailable())
                    {
                        if (it.status.equals(Status.SUCCESS))
                        {
                            val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                            it.data!!.data!!.listRequest!!.forEach{parti ->
                                partiList.add(parti!!)
                            }

                            if (partiList.size > 0)
                            {
                                participantListObject = partiList
                                adapter.submitList(participantListObject)
                                participantAdapter!!.notifyDataSetChanged()
                                participantListMeta = it.data.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                binding.progressBar.visibility = View.GONE
                                binding.noRecordsView.visibility = View.GONE
                            }
                            else
                            {
                                binding.noRecordsView.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                        else
                        {
                            adapter.submitList(emptyList())
                            //binding.progressBar.visibility = View.GONE
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
            }
        }

        binding.participantIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long)
            {
                if (position == 0)
                {
                    selectedStatus = "participant"
                }
                else
                {
                    selectedStatus = "status"
                }

                binding.statusSpinner.setSelection(0)

                homeViewModel.setFilterId(page = 1, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

                homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (isNetworkAvailable())
                    {
                        if (it.status.equals(Status.SUCCESS))
                        {
                            val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                            it.data!!.data!!.listRequest!!.forEach{parti ->
                                partiList.add(parti!!)
                            }

                            if (partiList.size > 0)
                            {
                                participantListObject = partiList
                                adapter.submitList(participantListObject)
                                participantAdapter!!.notifyDataSetChanged()
                                participantListMeta = it.data.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                binding.progressBar.visibility = View.GONE
                                binding.noRecordsView.visibility = View.GONE
                            }
                            else
                            {
                                binding.noRecordsView.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                        else
                        {
                            adapter.submitList(emptyList())
                            //binding.progressBar.visibility = View.GONE
                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                    }
                })

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

            }

        }

        binding.statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long)
            {
                if (position == 0)
                {
                    selectedStatusId = "all"
                }
                else if (position == 1)
                {
//                    if (selectedStation == "sample")
//                    {
//                        selectedStatusId = "1000"
//                    }
//                    else
//                    {
//                        selectedStatusId = "1"
//                    }
                    selectedStatusId = "1"
                }
                else if (position == 2)
                {
                    selectedStatusId = "10"
                }
                else if (position == 3)
                {
//                    if (selectedStation == "sample")
//                    {
//                        selectedStatusId = "10000"
//                    }
//                    else
//                    {
//                        selectedStatusId = "100"
//                    }
                    selectedStatusId = "100"
                }

                // to implement the new api for the not started stations

                if (selectedStatusId == "10")
                {

                    homeViewModel.setNotStartedFilterId(page = 1, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

                    homeViewModel.notStartedStations?.observe(activity!!, Observer {

                        if (isNetworkAvailable())
                        {
                            if (it.status.equals(Status.SUCCESS))
                            {
                                adapter.submitList(emptyList())
                                val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                                it.data!!.data!!.listRequest!!.forEach{parti ->
                                    partiList.add(parti!!)
                                }

                                if (partiList.size > 0)
                                {
                                    participantListObject = partiList
                                    adapter.submitList(participantListObject)
                                    participantAdapter!!.notifyDataSetChanged()
                                    participantListMeta = it.data.meta
                                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                    binding.progressBar.visibility = View.GONE
                                    binding.noRecordsView.visibility = View.GONE
                                }
                                else
                                {
                                    binding.noRecordsView.visibility = View.VISIBLE
                                    binding.progressBar.visibility = View.GONE
                                }
                            }
                            else
                            {
                                adapter.submitList(emptyList())
                                //binding.progressBar.visibility = View.GONE
                            }
                        }
                        else
                        {
                            Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                        }
                    })
                }
                else // to implement the started stations
                {
                    homeViewModel.setFilterId(page = 1, station=selectedStationId!!, status = selectedStatusId!!, date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

                    homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                        if (isNetworkAvailable())
                        {
                            if (it.status.equals(Status.SUCCESS))
                            {
                                val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                                it.data!!.data!!.listRequest!!.forEach{parti ->
                                    partiList.add(parti!!)
                                }

                                if (partiList.size > 0)
                                {
                                    participantListObject = partiList
                                    adapter.submitList(participantListObject)
                                    participantAdapter!!.notifyDataSetChanged()
                                    participantListMeta = it.data.meta
                                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                    binding.progressBar.visibility = View.GONE
                                    binding.noRecordsView.visibility = View.GONE
                                }
                                else
                                {
                                    binding.noRecordsView.visibility = View.VISIBLE
                                    binding.progressBar.visibility = View.GONE
                                }
                            }
                            else
                            {
                                adapter.submitList(emptyList())
                                //binding.progressBar.visibility = View.GONE
                            }
                        }
                        else
                        {
                            Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

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

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        inflater?.inflate(R.menu.menu_main, menu)
//        //checkConnection(menu!!)
//    }

    @SuppressLint("SetTextI18n")
    fun datePickerDialog(): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(context!!, R.style.datepicker, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val birthDate: Date = Date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))

            binding.lastMealDate.setText(sdf.format(cal.time))
            selectedDate = sdf.format(cal.time)
            binding.executePendingBindings()

            binding.statusSpinner.setSelection(0)

            homeViewModel.setFilterId(page = 1, station=selectedStationId!!, status = "all", date = binding.lastMealDate.text.toString(), sort = selectedStatus!!)

            homeViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                if (isNetworkAvailable())
                {
                    if (it.status.equals(Status.SUCCESS))
                    {
                        val partiList: ArrayList<ParticipantListItem> = ArrayList<ParticipantListItem>()

                        it.data!!.data!!.listRequest!!.forEach{parti ->
                            partiList.add(parti!!)
                        }

                        if (partiList.size > 0)
                        {
                            participantListObject = partiList
                            adapter.submitList(participantListObject)
                            participantAdapter!!.notifyDataSetChanged()
                            participantListMeta = it.data.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            binding.progressBar.visibility = View.GONE
                            binding.noRecordsView.visibility = View.GONE
                        }
                        else
                        {
                            binding.noRecordsView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                    else
                    {
                        adapter.submitList(emptyList())
                        //binding.progressBar.visibility = View.GONE
                    }
                }
                else
                {
                    Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                }
            })

        }, year, month, day)
        // Show Date Picker

        return datePickerDialog
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
