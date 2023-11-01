package org.nghru_uk.ghru.ui.dashboard.selectedstation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.databinding.SelectedStationItemBinding
import org.nghru_uk.ghru.ui.common.DataBoundListAdapter
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.ParticipantListItem
import org.nghru_uk.ghru.vo.StationItem


class SelectedStationAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((ParticipantListItem) -> Unit)?
) : DataBoundListAdapter<ParticipantListItem, SelectedStationItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<ParticipantListItem>() {
        override fun areItemsTheSame(oldItem: ParticipantListItem, newItem: ParticipantListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ParticipantListItem, newItem: ParticipantListItem): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.screening_id == newItem.screening_id
        }
    }
) {

    override fun createBinding(parent: ViewGroup): SelectedStationItemBinding {
        val binding = DataBindingUtil
            .inflate<SelectedStationItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.selected_station_item,
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

    override fun bind(binding: SelectedStationItemBinding, item: ParticipantListItem) {
        binding.homeItem = item

        val date :String? = item.registerd_date
        val newDate: String  = date!!.take(10)
        item.registerd_date = newDate

        if (item.isCancelled == 1)
        {
            item.statusId = R.drawable.status_cancel
        }
        else if (item.status.equals("100"))
        {
            item.statusId = R.drawable.ic_icon_status_tick
        }
        else if (item.status.equals("1"))
        {
            item.statusId = R.drawable.status_progress
        }
        else if (item.status.equals("1000"))
        {
            item.statusId = R.drawable.status_progress
        }
        else if (item.status.equals("10000"))
        {
            item.statusId = R.drawable.ic_icon_status_tick
        }
        else
        {
            item.statusId = R.drawable.ic_icon_status_warning_yellow
        }
    }
}
