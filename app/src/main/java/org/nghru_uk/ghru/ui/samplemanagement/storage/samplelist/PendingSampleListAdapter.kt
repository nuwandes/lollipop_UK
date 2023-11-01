package org.nghru_uk.ghru.ui.samplemanagement.storage.samplelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.databinding.SampleStorageItemBinding
import org.nghru_uk.ghru.ui.common.DataBoundListAdapter
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.request.SampleRequest


class PendingSampleListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val callback: ((SampleRequest) -> Unit)?
) : DataBoundListAdapter<SampleRequest, SampleStorageItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<SampleRequest>() {
            override fun areItemsTheSame(oldItem: SampleRequest, newItem: SampleRequest): Boolean {
                return oldItem.sampleId == newItem.sampleId
            }

            override fun areContentsTheSame(oldItem: SampleRequest, newItem: SampleRequest): Boolean {
                return oldItem.sampleId == newItem.sampleId
                        && oldItem.timestamp == newItem.timestamp
            }
        }
) {

    override fun createBinding(parent: ViewGroup): SampleStorageItemBinding {
        val binding = DataBindingUtil
                .inflate<SampleStorageItemBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.sample_storage_item,
                        parent,
                        false,
                        dataBindingComponent
                )
        binding.root.singleClick { it ->
            binding.sample.let {
                callback?.invoke(it!!)
            }
        }
        return binding
    }

    override fun bind(binding: SampleStorageItemBinding, item: SampleRequest) {
        binding.sample = item
    }
}
