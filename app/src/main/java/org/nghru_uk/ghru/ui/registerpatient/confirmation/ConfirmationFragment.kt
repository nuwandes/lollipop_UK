package org.nghru_uk.ghru.ui.registerpatient.confirmation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
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
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.ConfirmationFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.jobs.SyncImageConcentUploadJob
import org.nghru_uk.ghru.jobs.SyncImageUploadJob
import org.nghru_uk.ghru.jobs.SyncparticipantMetaJob
import org.nghru_uk.ghru.ui.registerpatient.confirmation.completed.CompletedDialogFragment
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.getLocalTimeString
import org.nghru_uk.ghru.util.hideKeyboard
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.request.ParticipantMeta
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ConfirmationFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<ConfirmationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var confirmationViewModel: ConfirmationViewModel

    @Inject
    lateinit var jobManager: JobManager

    private var participantMeta: ParticipantMeta? = null
    //private var concentPhoto: String? = null
    //private var concentPhotoNew: String? = null
    private var consentOneRepeat:Int? = 0
    private var consentTwoRepeat:Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //concentPhoto = arguments?.getString("concentPhotoPath")!!
            //concentPhotoNew = arguments?.getString("concentPhotoPathNew")!!
            participantMeta = arguments?.getParcelable<ParticipantMeta>("participantMeta")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ConfirmationFragmentBinding>(
            inflater,
            R.layout.confirmation_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()
        return dataBinding.root
    }


    private lateinit var participantRequest: ParticipantRequest

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //participantMeta?.body?.gender?.toLowerCase()

        val memberId = if (participantMeta?.body?.memberId != null) participantMeta?.body?.memberId!! else ""
        val householdIdX =
            if (participantMeta?.body?.enumerationId != null) participantMeta?.body?.enumerationId!! else ""
        binding.participantMeta = participantMeta


        binding.screeningId = participantMeta?.body?.screeningId!!

        val gender: String = participantMeta?.body?.gender.toString().toLowerCase()

//        val endTime: String = convertTimeTo24Hours()
//        val endDate: String = getDate()
//        val endDateTime:String = endDate + " " + endTime
//
//        participantMeta?.meta?.endTime = endDateTime
        participantMeta?.body?.gender = gender

        confirmationViewModel.participantMetaSaveRemote?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS)
            {
                binding.progressBar.visibility = View.GONE
                binding.confirmButton.visibility = View.VISIBLE
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("participantMeta" to participantMeta)
                completedDialogFragment.show(fragmentManager!!)

//                confirmationViewModel.setUploadConcent(concentPhoto, participantMeta?.body?.screeningId!!)
            }
            else if (participant?.status == Status.ERROR)
            {
                Crashlytics.setString("participantRequest", participantRequest.toString())
                Crashlytics.setString("participantMeta", participantMeta.toString())
                Crashlytics.logException(Exception("participantMetaSaveRemote " + participant.message?.error.toString()))
                Crashlytics.logException(Exception("participant.message?.data?.message " + participant.message?.data?.message))
                binding.progressBar.visibility = View.GONE
                binding.textViewError.setText(participant.message?.data?.message)
                binding.textViewError.visibility = View.VISIBLE
                binding.executePendingBindings()
            }
        })

        confirmationViewModel.uploadConcent?.observe(this, Observer { upload ->

            if (upload?.status == Status.SUCCESS) {
                Crashlytics.logException(Exception("uploadConcent sucess"))

//                confirmationViewModel.setUploadConsentNew(concentPhotoNew, participantMeta?.body?.screeningId!!)
                Log.d("REGISTRATION_CONFIRM", "FIRST UPLOAD SUCCESS: ")

            } else if (upload?.status == Status.ERROR)
            {
//                consentOneRepeat = consentOneRepeat!!.plus(1)
//
//                Log.d("REGISTRATION_CONFIRM", "FIRST_FAILD: " + consentOneRepeat )
//
//                //Toast.makeText(activity!!, "AAAAA", Toast.LENGTH_LONG).show()
//
//                if (consentOneRepeat!! <= 3 && consentOneRepeat!! > 0)
//                {
//                    Log.d("REGISTRATION_CONFIRM", "FIRST_FAILD _AND_INSIDE_IF: " + consentOneRepeat )
//                    confirmationViewModel.setUploadConcent(concentPhoto, participantMeta?.body?.screeningId!!)
//                }
//                else
//                {
//                    Log.d("REGISTRATION_CONFIRM", "FIRST_FAILD _AND_OUTSIDE_IF: " + consentOneRepeat )
//                    Toast.makeText(activity!!, "First consent form upload failed. Please try again.", Toast.LENGTH_LONG).show()
//                    binding.progressBar.visibility = View.GONE
//                    binding.textViewError.setText(upload.message?.message)
//                    binding.textViewError.visibility = View.VISIBLE
//                    binding.executePendingBindings()
//                }
//                Toast.makeText(activity!!, "First consent form upload failed. Please try again.", Toast.LENGTH_LONG).show()
//                binding.progressBar.visibility = View.GONE
//                binding.textViewError.setText(upload.message?.message)
//                binding.textViewError.visibility = View.VISIBLE
//                binding.executePendingBindings()
            }
        })

//        confirmationViewModel.uploadConsentNew?.observe(this, Observer { upload ->
//            //println(upload)
//            if (upload?.status == Status.SUCCESS) {
//                Crashlytics.logException(Exception("uploadConcent sucess"))
//
//                Toast.makeText(activity!!, "Two consent forms uploaded successfully.", Toast.LENGTH_LONG).show()
//                Log.d("REGISTRATION_CONFIRM", "SECOND SUCCESS: ")
//
//                binding.progressBar.visibility = View.GONE
//                binding.confirmButton.visibility = View.VISIBLE
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.show(fragmentManager!!)
//
//            } else if (upload?.status == Status.ERROR) {
//
//                consentTwoRepeat = consentTwoRepeat!!.plus(1)
//                Log.d("REGISTRATION_CONFIRM", "SECOND_FAILD: " + consentTwoRepeat )
//                //Toast.makeText(activity!!, "BBBBB", Toast.LENGTH_LONG).show()
//
//                if (consentTwoRepeat!! <= 3 && consentTwoRepeat!! >0)
//                {
//                    Log.d("REGISTRATION_CONFIRM", "SECOND_FAILD _AND_INSIDE_IF: " + consentTwoRepeat )
//                    confirmationViewModel.setUploadConsentNew(concentPhotoNew, participantMeta?.body?.screeningId!!)
//                }
//                else
//                {
//                    Log.d("REGISTRATION_CONFIRM", "SECOND_FAILD _AND_OUTSIDE_IF: " + consentTwoRepeat )
//                    Toast.makeText(activity!!, "Second consent form upload failed. Please try again.", Toast.LENGTH_LONG).show()
//                    binding.progressBar.visibility = View.GONE
//                    binding.textViewError.setText(upload.message?.message)
//                    binding.textViewError.visibility = View.VISIBLE
//                    binding.executePendingBindings()
//                }
////                Toast.makeText(activity!!, "Second consent form upload failed. Please try again.", Toast.LENGTH_LONG).show()
////                binding.progressBar.visibility = View.GONE
////                binding.textViewError.setText(upload.message?.message)
////                binding.textViewError.visibility = View.VISIBLE
////                binding.executePendingBindings()
//            }
//        })

        binding.confirmButton.singleClick {
            binding.progressBar.visibility = View.VISIBLE
            binding.confirmButton.visibility = View.GONE
            participantMeta?.body?.comment = binding.comment.text.toString()

            val endTime: String = convertTimeTo24Hours()
            val endDate: String = getDate()
            val endDateTime:String = endDate + " " + endTime

            participantMeta?.meta?.endTime = endDateTime

            participantRequest = ParticipantRequest(
                firstName = participantMeta?.body?.firstName!!,
                lastName = participantMeta?.body?.lastName!!,
                age = participantMeta?.body?.age!!,
                gender = participantMeta?.body?.gender!!.toString().toLowerCase(),
                idNumber = participantMeta?.body?.idNumber!!,
                fatherName = "fathers name",
                idType = "NID",
                screeningId = participantMeta?.body?.screeningId!!,
                householdId = householdIdX,
                memberId = memberId,
                profileImage = "",
                identityImage = participantMeta?.body?.identityImage,
                contactNumber = participantMeta?.body?.contactDetails?.phoneNumberPreferred!!,
                comment = participantMeta?.body?.comment

            )

//            val sTime: String = convertTimeTo24Hours()
//            val sDate: String = getDate()
//            val sDateTime:String = sDate + " " + sTime

            participantRequest.createdDateTime = endDateTime
            if (isNetworkAvailable()) {
                participantRequest.syncPending = false
            } else {
                participantRequest.syncPending = true
            }

            confirmationViewModel.setParticipantMetaRemote(participantMeta!!)
            //confirmationViewModel.setUploadConcent(concentPhoto, participantMeta?.body?.screeningId!!)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().popBackStack()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun convertTimeTo24Hours(): String
    {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(now.time.toLocaleString())
            output = outputformat.format(date)
            return output
        }catch(p: ParseException){
            return ""
        }
    }

    private fun getDate(): String
    {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val outputformat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            return output
        }catch(p: ParseException){
            return ""
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
