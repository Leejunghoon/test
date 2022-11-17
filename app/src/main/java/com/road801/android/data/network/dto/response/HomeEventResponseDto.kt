package com.road801.android.data.network.dto.response
import com.road801.android.data.network.dto.EventDto

data class HomeEventResponseDto(
    val popup: EventDto,
    val eventList: List<EventDto>
)
