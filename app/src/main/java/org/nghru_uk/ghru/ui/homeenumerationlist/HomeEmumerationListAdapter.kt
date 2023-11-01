package org.nghru_uk.ghru.ui.homeenumerationlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.databinding.HomeEmumerationListItemBinding
import org.nghru_uk.ghru.ui.common.DataBoundListAdapter
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.HomeEmumerationListItem


class HomeEmumerationListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((HomeEmumerationListItem) -> Unit)?
) : DataBoundListAdapter<HomeEmumerationListItem, HomeEmumerationListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HomeEmumerationListItem>() {
        override fun areItemsTheSame(oldItem: HomeEmumerationListItem, newItem: HomeEmumerationListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeEmumerationListItem, newItem: HomeEmumerationListItem): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
        }
    }
) {

    override fun createBinding(parent: ViewGroup): HomeEmumerationListItemBinding {
        val binding = DataBindingUtil
            .inflate<HomeEmumerationListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.home_emumeration_list_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.homeEmumerationListItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: HomeEmumerationListItemBinding, item: HomeEmumerationListItem) {
        binding.homeEmumerationListItem = item
    }
}
