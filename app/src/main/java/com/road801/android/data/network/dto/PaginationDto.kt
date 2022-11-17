package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaginationDto(
    val page: Int,
    val size: Int,
    val sort: List<String>
) : Parcelable