package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.road801.android.common.util.extension.formatted
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.databinding.ItemEventBinding

class NewsRecyclerAdapter(private val items: List<NewsDto>,
                           private val onClick: ((item: NewsDto) -> Unit)? = null): RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsDto) {
            binding.itemEventTitleTextView.text = item.title
            binding.itemEventSubTextView.visibility = View.GONE
            binding.itemEventDateTextView.text = item.writeDt.formatted("yyyy.MM.dd")

            if(item.thumbnail.isNullOrEmpty().not()) {
                binding.itemEventImageView.visibility = View.VISIBLE
                Glide.with(binding.itemEventImageView.context)
                    .load(item.thumbnail)
                    .into(binding.itemEventImageView)
            }

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}