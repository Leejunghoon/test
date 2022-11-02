package com.road801.android.data.network.dto

import android.os.Parcelable
import com.road801.android.common.enum.GenderType
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDto (
    val name: String,
    val birthday: String?,
    val mobileNo: String?,
    val sexType: GenderType,
    val termAgreeList: List<String>,
    val socialType: String?,
    val socialId: Long?,
    val loginId: String?,
    val password: String?,
    val thumbnailImageUrl: String?
) : Parcelable