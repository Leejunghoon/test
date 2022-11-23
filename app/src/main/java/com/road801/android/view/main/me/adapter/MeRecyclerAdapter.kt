package com.road801.android.view.main.me.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.road801.android.common.enum.RecyclerViewType
import com.road801.android.data.network.dto.MeDto
import com.road801.android.databinding.ItemMeBinding
import com.road801.android.databinding.ItemMeHeaderBinding
import com.road801.android.domain.model.SettingModel
import com.road801.android.view.main.me.MeFragment
import com.road801.android.view.main.me.MeFragmentDirections


class MeRecyclerAdapter(
    private val fragment: MeFragment,
    private val meDto: MeDto,
    private val settingModels: List<SettingModel>,
    private val onClick: ((item: SettingModel) -> Unit)? = null
) : RecyclerView.Adapter<ViewHolder>() {
    private val HEADER_SIZE = 1

    override fun getItemCount(): Int = HEADER_SIZE + settingModels.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            RecyclerViewType.HEADER.ordinal -> return HeaderViewHolder(ItemMeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//            RecyclerViewType.FOOTER.ordinal -> {}
        }
        return BodyViewHolder(ItemMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            RecyclerViewType.HEADER.ordinal -> return RecyclerViewType.HEADER.ordinal
//            RecyclerViewType.FOOTER.ordinal -> return RecyclerViewType.FOOTER.ordinal
        }
        return RecyclerViewType.BODY.ordinal
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            RecyclerViewType.HEADER.ordinal -> (holder as HeaderViewHolder).bind(meDto)
            RecyclerViewType.BODY.ordinal -> (holder as BodyViewHolder).bind(settingModels[position - 1])
//            RecyclerViewType.FOOTER.ordinal -> null
        }
    }

    // 헤더 뷰홀더
    inner class HeaderViewHolder(private val binding: ItemMeHeaderBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MeDto) {
            binding.itemMeHeaderNameTextView.text = item.name
            binding.itemMeHeaderEmailTextView.text = item.mobileNo

            item.profileImage?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop()
                    .into(binding.itemMeHeaderProfileImageView)
            }

            binding.itemMeHeaderModifyButton.setOnClickListener {
                fragment.findNavController().navigate(MeFragmentDirections.actionMeFragmentToProfileFragment(item))
            }

        }
    }

    // 바디 뷰홀더
    inner class BodyViewHolder(private val binding: ItemMeBinding) : ViewHolder(binding.root) {

        fun bind(item: SettingModel) {
            binding.itemMeTitleTextView.text = item.title

            Glide.with(binding.root.context)
                .load(item.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.itemMeImageView)

            itemView.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }



}