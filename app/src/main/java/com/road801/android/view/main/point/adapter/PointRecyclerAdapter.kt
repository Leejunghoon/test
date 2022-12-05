package com.road801.android.view.main.point.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.road801.android.R
import com.road801.android.common.enum.PointType
import com.road801.android.common.util.extension.formatted
import com.road801.android.data.network.dto.PointHistoryDto
import com.road801.android.databinding.ItemPointBinding

class PointRecyclerAdapter(private val items: List<PointHistoryDto>,
                           private val onClick: ((item: PointHistoryDto) -> Unit)? = null): RecyclerView.Adapter<PointRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemPointBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemPointBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PointHistoryDto) {
            val context = binding.root.context
            binding.itemPointTitleTextView.text = item.storeName
            binding.itemPointProductNameTextView.text = item.productName
            binding.itemPointDateTextView.text = item.requestDt.formatted("yyyy년 MM월 dd일 (E)")
            binding.itemPointPointTextView.text = "${item.point}P ${item.logType.value}"

            when(PointType.valueOf(item.logType.code)) {
                PointType.EARN -> {
                    binding.itemPointPointTextView.setTextColor(ContextCompat.getColor(context, R.color.primary))
                }
                PointType.USE -> {
                    binding.itemPointPointTextView.setTextColor(ContextCompat.getColor(context, R.color.alert_red))
                }
                PointType.EARN_CANCEL, PointType.USE_CANCEL -> {
                    binding.itemPointPointTextView.setTextColor(ContextCompat.getColor(context, R.color.primary_gray))
                }
            }


            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}