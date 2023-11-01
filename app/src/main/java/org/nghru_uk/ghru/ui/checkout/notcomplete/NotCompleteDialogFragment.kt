package org.nghru_uk.ghru.ui.checkout.notcomplete

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.binding.FragmentDataBindingComponent
import org.nghru_uk.ghru.databinding.NotCompleteDialogFragmentBinding
import org.nghru_uk.ghru.di.Injectable
import org.nghru_uk.ghru.util.autoCleared
import org.nghru_uk.ghru.util.getLocalTimeString
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Meta
import org.nghru_uk.ghru.vo.Status
import org.nghru_uk.ghru.vo.request.CheckoutRequestUK
import org.nghru_uk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import androidx.lifecycle.Observer

class NotCompleteDialogFragment : DialogFragment(), Injectable {

    val TAG = NotCompleteDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<NotCompleteDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var confirmationdialogViewModel: NotCompleteDialogViewModel

    var isCancel: Boolean = false
    //var meta: Meta? = null

    private var participantRequest: ParticipantRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //isCancel = arguments?.getBoolean("is_cancel")!!
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            //meta = arguments?.getParcelable<Meta>("Meta")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<NotCompleteDialogFragmentBinding>(
            inflater,
            R.layout.not_complete_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isCancel = isCancel!!

        if(isCancel){
            binding.textView1.text = getString(R.string.station_canceled)
        }else{
            binding.textView1.text = getString(R.string.not_complete_checkout_msg1)
        }

        Log.d("DIALOG_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)
        binding.proceedAnywayButton.singleClick{

            if (participantRequest != null)
            {
                Log.d("DIALOG_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

                Log.d("DIALOG_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val chkRequest = CheckoutRequestUK(meta = participantRequest?.meta, comment="")
                chkRequest.screeningId = participantRequest?.screeningId!!
                if(isNetworkAvailable()){
                    chkRequest.syncPending =false
                }else{
                    chkRequest.syncPending =true

                }

                Log.d("FFQ_CONFIRAMTION", "DATA:" + chkRequest)

                confirmationdialogViewModel.setPostChk(chkRequest, participantRequest!!.screeningId)

            }
        }
        binding.homeButton.singleClick {
            activity?.finish()
            dismiss()
        }

        binding.textView2.text = getString(R.string.not_complete_checkout_msg2)

        confirmationdialogViewModel.chkPostComplete?.observe(this, Observer { chkPocess ->

            if (chkPocess?.status == Status.SUCCESS)
            {
                val bundle = bundleOf("ParticipantRequest" to participantRequest)
                findNavController().navigate(R.id.action_global_CheckoutCompletionFragment, bundle)
                dismiss()
            }
            else if(chkPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "CheckoutRequest",
                    CheckoutRequestUK(meta = participantRequest?.meta, comment = "").toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + chkPocess.message.toString()))
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

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

}
