package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CodeDto(
    val code: String,
    val value: String
) : Parcelable
