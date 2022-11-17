package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.road801.android.common.util.extension.formatted
import com.road801.android.data.network.dto.EventDto
import com.road801.android.databinding.ItemHomeEventBinding

/**
 * MARK: - 홈 화면 이벤트 어댑터
 *
 */
class HomeEventPagerAdapter(private val items: List<EventDto>,
                            private val onClick: ((item: EventDto) -> Unit)? = null): RecyclerView.Adapter<HomeEventPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemHomeEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemHomeEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventDto) {
            binding.itemHomeEventTitleTextView.text = item.title
            binding.itemHomeEventDateTextView.text = buildString {
                append(item.startDt?.formatted("기간 "))
                append(item.startDt?.formatted("yyyy.MM.dd"))
                append(" ~ ")
                append(item.endDt?.formatted("yyyy.MM.dd"))
            }

            Glide.with(binding.itemHomeEventImageView.context)
                .load(item.image)
                .into(binding.itemHomeEventImageView)

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}
