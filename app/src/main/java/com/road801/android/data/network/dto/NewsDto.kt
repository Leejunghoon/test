package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsDto(
    val id: Int,
    val title: String,
    val subtitle: String,
    val categoryName: String,
    val iconUrl: String,
    val date: String
) : Parcelable

