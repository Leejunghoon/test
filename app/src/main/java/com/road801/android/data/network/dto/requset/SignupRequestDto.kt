package com.road801.android.data.network.dto.requset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignupRequestDto (
    val name: String?,
    val birthday: String?,
    val mobileNo: String?,
    val sexType: String?,
    val termAgreeList: ArrayList<String>,
    val socialType: String?,
    val socialId: String?,
    val loginId: String?,
    val password: String?,
): Parcelable