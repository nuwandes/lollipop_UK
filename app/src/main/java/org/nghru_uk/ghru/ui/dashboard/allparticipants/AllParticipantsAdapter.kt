package org.nghru_uk.ghru.ui.dashboard.allparticipants

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.databinding.ParticipantStationsItemBinding
import org.nghru_uk.ghru.ui.common.DataBoundListAdapter
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.ParticipantStationItemNew
import org.nghru_uk.ghru.vo.ParticipantStationItemNewNew
import org.nghru_uk.ghru.vo.ParticipantStationsItem


class AllParticipantsAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val context: Context,
    private val callback: ((ParticipantStationItemNewNew) -> Unit)?
) : DataBoundListAdapter<ParticipantStationItemNewNew, ParticipantStationsItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<ParticipantStationItemNewNew>() {
        override fun areItemsTheSame(oldItem: ParticipantStationItemNewNew, newItem: ParticipantStationItemNewNew): Boolean {
            return oldItem.participant_id == newItem.participant_id
        }

        override fun areContentsTheSame(oldItem: ParticipantStationItemNewNew, newItem: ParticipantStationItemNewNew): Boolean {
            return oldItem.participant_id == newItem.participant_id
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ParticipantStationsItemBinding {
        val binding = DataBindingUtil
            .inflate<ParticipantStationsItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.participant_stations_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.homeItem?.let {
                callback?.invoke(it)

            }
        }
        return binding
    }

    override fun bind(binding: ParticipantStationsItemBinding, item: ParticipantStationItemNewNew) {
        binding.homeItem = item

        binding.registerIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.sampleIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.spiroIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.bpIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.intIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.chkIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.bmIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.ecgIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.fundoIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.axiIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
        binding.paHlqIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)

        for (stationItem in item.listRequest!!)
        {
            if (stationItem!!.station_name.equals("Registration"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.registerIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.registerIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.registerIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.registerIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.registerIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem.station_name.equals("Biological Samples"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1000)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
                else if(stationItem.status_code!!.toInt() == 10000)
                {
                    binding.sampleIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.sampleIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.sampleIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Body Measurements"))
            {
                if(stationItem.isCancelled == 1)
                {
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                    binding.bmIcon.setBackgroundResource(R.drawable.status_cancel)
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.bmIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.bmIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.bmIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.bmIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("ECG"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.ecgIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.ecgIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.ecgIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.ecgIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.ecgIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Spirometry"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.spiroIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.spiroIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.spiroIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.spiroIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.spiroIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Fundoscopy"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.fundoIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.fundoIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.fundoIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.fundoIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.fundoIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Blood Pressure"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.bpIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.bpIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.bpIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.bpIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.bpIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Intake 24"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.intIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.intIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.intIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.intIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.intIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Participant HLQ"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.paHlqIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.paHlqIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.paHlqIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.paHlqIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.paHlqIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem!!.station_name.equals("Axivity"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.axiIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.axiIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.axiIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.axiIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.axiIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }
            else if (stationItem.station_name.equals("Checkout"))
            {
                if(stationItem.isCancelled == 1)
                {
                    binding.chkIcon.setBackgroundResource(R.drawable.status_cancel)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_cancel))
                }
                else if(stationItem.status_code!!.toInt() == 1)
                {
                    binding.chkIcon.setBackgroundResource(R.drawable.status_progress)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_progress))
                }
                else if(stationItem.status_code!!.toInt() == 100)
                {
                    binding.chkIcon.setBackgroundResource(R.drawable.status_complete)
                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.status_complete))
                }
//                else
//                {
//                    binding.chkIcon.setBackgroundResource(R.drawable.ic_icon_status_warning_yellow)
//                    //binding.repIcon.setImageDrawable(context!!.getDrawable(R.drawable.ic_icon_status_warning_yellow))
//                }
            }

        }
    }
}
