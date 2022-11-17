package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsDetailDto(
    val subtitle: String,
    val content: String,
    val image: String
) : Parcelable
