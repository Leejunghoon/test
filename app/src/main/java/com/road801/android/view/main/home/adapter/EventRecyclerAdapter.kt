package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.road801.android.common.util.extension.formatted
import com.road801.android.data.network.dto.EventDto
import com.road801.android.databinding.ItemEventBinding

class EventRecyclerAdapter(private val items: List<EventDto>,
                            private val onClick: ((item: EventDto) -> Unit)? = null): RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventDto) {
            binding.itemEventTitleTextView.text = item.title
            binding.itemEventDateTextView.text = buildString {
                append(item.startDt?.formatted("기간 "))
                append(item.startDt?.formatted("yyyy.MM.dd"))
                append(" ~ ")
                append(item.endDt?.formatted("yyyy.MM.dd"))
            }
            binding.itemEventContentTextView.text = item.content

            item.thumbnail?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.itemEventImageView)
            }


            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}