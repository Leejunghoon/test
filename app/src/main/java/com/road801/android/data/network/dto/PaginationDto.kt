package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaginationDto(
    var nextId: String? = null,
    var size: Int = 20,
    var sort: List<String>
) : Parcelable