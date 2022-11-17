package com.road801.android.data.network.dto.response

data class CommonListResponseDto<T>(
    val data: List<T>,
    val total: Int = 0
)
