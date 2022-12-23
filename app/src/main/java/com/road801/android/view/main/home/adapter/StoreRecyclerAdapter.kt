package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.road801.android.data.network.dto.StoreDto
import com.road801.android.databinding.ItemEventBinding
import com.road801.android.databinding.ItemStoreBinding

class StoreRecyclerAdapter(private val items: List<StoreDto>,
                          private val onClick: ((item: StoreDto) -> Unit)? = null): RecyclerView.Adapter<StoreRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemStoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreDto) {
            binding.itemStoreTitleTextView.text = item.name

            if(item.thumbnail.isNullOrEmpty().not()) {
                Glide.with(binding.root.context)
                    .load(item.thumbnail)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.itemStoreImageView)
            }

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}