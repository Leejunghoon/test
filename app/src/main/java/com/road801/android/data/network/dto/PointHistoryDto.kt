package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointHistoryDto(
    val storeName: String,
    val logType: GradeDto,
    val requestDt: String,
    val point: Int
) : Parcelable
