package com.road801.android.data.network.dto.requset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WithdrawalRequestDto (
    val dropReason: String
): Parcelable