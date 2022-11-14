package com.road801.android.data.network.dto.requset

data class LoginRoadRequestDto (
    val loginId: String,
    val password: String
)

data class LoginSNSRequestDto (
    val socialType: String,
    val socialId: String
)