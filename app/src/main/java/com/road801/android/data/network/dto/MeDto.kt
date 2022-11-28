package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeDto(
    val customerBarcode: String,
    val name: String,
    val birthday: String,
    val mobileNo: String,
    val profileImage: String?,
    val sexType: CodeDto,
    val signupType: CodeDto,
    val socialType: CodeDto
) : Parcelable
