package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaginationDto(
    var page: Int,
    var size: Int,
    var sort: List<String>
) : Parcelable