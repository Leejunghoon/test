package com.road801.android.view.main.me.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.road801.android.common.enum.RecyclerViewType
import com.road801.android.databinding.ItemMeBinding
import com.road801.android.databinding.ItemMeHeaderBinding
import com.road801.android.domain.model.SettingModel


class MeRecyclerAdapter(private val items: List<SettingModel>,
                        private val onClick: ((item: SettingModel) -> Unit)? = null): RecyclerView.Adapter<ViewHolder>() {
    private val HEADER_SIZE = 1

    override fun getItemCount(): Int = HEADER_SIZE + items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("hooonn onCreateViewHolder ", viewType.toString())
        when(viewType) {
            RecyclerViewType.HEADER.ordinal -> HeaderViewHolder(ItemMeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//            RecyclerViewType.FOOTER.ordinal -> {}
        }
        return BodyViewHolder(ItemMeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun getItemViewType(position: Int): Int {
        Log.d("hooon getItemViewType11 ", position.toString())
        when(position) {
            RecyclerViewType.HEADER.ordinal -> RecyclerViewType.HEADER.ordinal
//            RecyclerViewType.FOOTER.ordinal -> RecyclerViewType.FOOTER.ordinal
        }
        return RecyclerViewType.BODY.ordinal
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("hooon onBindViewHolder ", position.toString())
        Log.d("hooon getItemViewType ", getItemViewType(position).toString())
        when(getItemViewType(position)) {
            RecyclerViewType.HEADER.ordinal -> (holder as HeaderViewHolder).bind()
            RecyclerViewType.BODY.ordinal -> (holder as BodyViewHolder).bind(items[0])
//            RecyclerViewType.FOOTER.ordinal -> null
        }
    }

    inner class HeaderViewHolder(private val binding: ItemMeHeaderBinding) : ViewHolder(binding.root) {
        fun bind() {
        }
    }

    inner class BodyViewHolder(private val binding: ItemMeBinding) : ViewHolder(binding.root) {
        fun bind(item: SettingModel) {

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}