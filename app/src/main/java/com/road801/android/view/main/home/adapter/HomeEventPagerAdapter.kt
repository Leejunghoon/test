package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.road801.android.data.network.dto.EventDto
import com.road801.android.databinding.ItemHomeEventBinding

/**
 * MARK: - 홈 화면 이벤트 어댑터
 *
 */
class HomeEventPagerAdapter(private val onClick: ((item: EventDto) -> Unit)? = null): RecyclerView.Adapter<HomeEventPagerAdapter.ViewHolder>() {

    private var items: List<EventDto> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = ItemHomeEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemHomeEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventDto) {
            binding.itemHomeEventTitleTextView.text = item.title
//            binding.itemHomeEventDateTextView.text = buildString {
//                append(item.startDt?.formatted("기간 "))
//                append(item.startDt?.formatted("yyyy.MM.dd"))
//                append(" ~ ")
//                append(item.endDt?.formatted("yyyy.MM.dd"))
//            }

            item.thumbnail?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.itemHomeEventImageView)
            }

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }

    public fun setItems(items: List<EventDto>) {
        this.items = items
        notifyDataSetChanged()
    }

}
