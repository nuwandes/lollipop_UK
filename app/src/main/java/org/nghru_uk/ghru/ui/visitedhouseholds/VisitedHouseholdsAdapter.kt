package org.nghru_uk.ghru.ui.visitedhouseholds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.databinding.HouseHoldItemBinding
import org.nghru_uk.ghru.ui.common.DataBoundListAdapter
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.request.HouseholdRequest


class VisitedHouseholdRequestAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((HouseholdRequest) -> Unit)?
) : DataBoundListAdapter<HouseholdRequest, HouseHoldItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HouseholdRequest>() {
        override fun areItemsTheSame(oldItem: HouseholdRequest, newItem: HouseholdRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HouseholdRequest, newItem: HouseholdRequest): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.timestamp == newItem.timestamp
        }
    }
) {

    override fun createBinding(parent: ViewGroup): HouseHoldItemBinding {
        val binding = DataBindingUtil
            .inflate<HouseHoldItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.house_hold_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.household?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: HouseHoldItemBinding, item: HouseholdRequest) {
        binding.household = item
    }
}
