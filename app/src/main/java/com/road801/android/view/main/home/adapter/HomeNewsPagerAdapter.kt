package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.road801.android.common.util.extension.formatted
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.databinding.ItemHomeEventBinding

/**
 * MARK: - 홈 화면 소식 어댑터
 *
 */
class HomeNewsPagerAdapter(private val onClick: ((item: NewsDto) -> Unit)? = null) :
    RecyclerView.Adapter<HomeNewsPagerAdapter.ViewHolder>() {

    private var items: List<NewsDto> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemHomeEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemHomeEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsDto) {
            binding.itemHomeEventTitleTextView.text = item.title
            binding.itemHomeEventDateTextView.text = buildString {
                append(item.writeDt?.formatted("기간 "))
                append(item.writeDt?.formatted("yyyy.MM.dd"))
                append(" ~ ")
                append(item.writeDt?.formatted("yyyy.MM.dd"))
            }

            item.thumbnail?.let {
                Glide.with(binding.itemHomeEventImageView.context)
                    .load(it)
                    .into(binding.itemHomeEventImageView)
            }

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }

    public fun setItems(items: List<NewsDto>) {
        this.items = items
        notifyDataSetChanged()
    }

}
