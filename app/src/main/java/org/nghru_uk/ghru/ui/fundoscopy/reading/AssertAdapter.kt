package org.nghru_uk.ghru.ui.fundoscopy.reading

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_uk.ghru.AppExecutors
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.databinding.FundoscopyAssetItemBinding
import org.nghru_uk.ghru.ui.common.DataBoundListAdapter
import org.nghru_uk.ghru.util.singleClick
import org.nghru_uk.ghru.vo.Asset


class AssetAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Asset) -> Unit)?
) : DataBoundListAdapter<Asset, FundoscopyAssetItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Asset>() {
        override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.url == newItem.url
        }
    }
) {

    override fun createBinding(parent: ViewGroup): FundoscopyAssetItemBinding {
        val binding = DataBindingUtil
            .inflate<FundoscopyAssetItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.fundoscopy_asset_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.asset?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: FundoscopyAssetItemBinding, item: Asset) {
        binding.asset = item
    }
}
