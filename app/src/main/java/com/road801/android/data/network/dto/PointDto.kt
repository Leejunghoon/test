package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointDto(
    val point: Int,                 // 사용가능 포인트
    val nextExpirePoint: Int,       // 소멸 예정 포인트
    val nextExpireDate: String?,    // 소멸 예정일
    val receivedPoint: Int          // 적립 받은 포인트
): Parcelable

