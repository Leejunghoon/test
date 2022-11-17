package com.road801.android.data.network.dto.response

import com.road801.android.data.network.dto.CustomerDto
import com.road801.android.data.network.dto.PointDto

data class HomeResponseDto(
    val customerInfo: CustomerDto,
    val pointInfo: PointDto,
    val alertCount: Int
)
