package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventDto(
    val id: Int,
    val title: String,
    val content: String,
    val thumbnail: String?,
    val image: String?,
    val startDt: String?,
    val endDt: String?,
    val isPromotion: Boolean    // 프로모션 여부(포인트 받기 버튼 활성화 여부)
): Parcelable
