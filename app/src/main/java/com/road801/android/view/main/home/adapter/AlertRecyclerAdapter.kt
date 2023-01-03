package com.road801.android.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.road801.android.common.util.extension.formatted
import com.road801.android.data.network.dto.AlertDto
import com.road801.android.databinding.ItemAlertBinding

class AlertRecyclerAdapter(private val items: List<AlertDto>,
                          private val onClick: ((item: AlertDto) -> Unit)? = null): RecyclerView.Adapter<AlertRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertDto) {
            binding.itemAlertTitleTextView.text = item.content
            binding.itemAlertDateTextView.text = item.createDt.formatted("yyyy.MM.dd")
        }
    }
}