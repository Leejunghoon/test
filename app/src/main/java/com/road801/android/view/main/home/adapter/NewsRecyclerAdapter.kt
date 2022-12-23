package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.road801.android.common.util.extension.formatted
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.databinding.ItemEventBinding
import com.road801.android.databinding.ItemNewsBinding

class NewsRecyclerAdapter(private val items: List<NewsDto>,
                           private val onClick: ((item: NewsDto) -> Unit)? = null): RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsDto) {
            binding.itemNewsTitleTextView.text = item.title
            binding.itemNewsDateTextView.text = item.writeDt?.formatted("yyyy.MM.dd")
            binding.itemNewsContentTextView.text = item.content

            if(item.thumbnail.isNullOrEmpty().not()) {
                Glide.with(binding.root.context)
                    .load(item.thumbnail)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.itemNewsImageView)
            }

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}