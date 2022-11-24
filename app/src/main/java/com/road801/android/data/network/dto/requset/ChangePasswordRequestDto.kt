package com.road801.android.data.network.dto.requset

data class ChangePasswordRequestDto(
    val mobileNo: String,
    val authValue: String,
    val password: String
)
