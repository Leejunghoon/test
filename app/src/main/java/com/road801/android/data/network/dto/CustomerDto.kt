package com.road801.android.data.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerDto(
    val name: String,
    val barcode: String,
    val profileImage: String,
    val rating: GradeDto
): Parcelable
