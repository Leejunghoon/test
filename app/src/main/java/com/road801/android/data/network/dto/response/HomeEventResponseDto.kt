package com.road801.android.data.network.dto.response
import com.road801.android.data.network.dto.EventDto
import com.road801.android.data.network.dto.NewsDto

data class HomeEventResponseDto(
    val popup: EventDto?,
    val eventList: List<EventDto>,
    val boardList: List<NewsDto>
)
