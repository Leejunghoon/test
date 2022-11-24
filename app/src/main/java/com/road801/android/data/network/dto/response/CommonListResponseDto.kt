package com.road801.android.data.network.dto.response

class CommonListResponseDto<T>(
    var data: List<T>,
    val total: Int = 0
)
