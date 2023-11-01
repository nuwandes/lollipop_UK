package org.nghru_uk.ghru.ui.samplemanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.databinding.NghruItemBinding
import org.nghru_uk.ghru.ui.common.DataBoundListAdapter
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.HomeItem


class SampleMangementAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((HomeItem) -> Unit)?
) : DataBoundListAdapter<HomeItem, NghruItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HomeItem>() {
        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
        }
    }
) {

    override fun createBinding(parent: ViewGroup): NghruItemBinding {
        val binding = DataBindingUtil
            .inflate<NghruItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.nghru_item,
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

    override fun bind(binding: NghruItemBinding, item: HomeItem) {
        binding.homeItem = item
    }
}
