package com.road801.android.data.network.dto

data class ErrorResponseDto(
    val code: String,
    val message: String,
    val content: String?
)