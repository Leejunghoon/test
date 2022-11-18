package com.road801.android.data.network.dto.requset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeRequestDto(
    val name: String,
    val birthday: String,
    val sexType: String
) : Parcelable