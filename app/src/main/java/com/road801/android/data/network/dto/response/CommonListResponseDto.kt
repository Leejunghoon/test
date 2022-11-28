package com.road801.android.data.network.dto.response

import com.road801.android.data.network.dto.MetaDto

class CommonListResponseDto<T>(
    var meta: MetaDto,
    var data: List<T>,
    val total: Int = 0
)
