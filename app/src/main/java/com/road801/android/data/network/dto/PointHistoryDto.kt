package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointHistoryDto(
    val productName: String,
    val storeName: String,
    val logType: CodeDto,
    val requestDt: String,
    val point: Int
) : Parcelable
