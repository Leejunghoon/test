package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsDto(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val writeDt: String
) : Parcelable

