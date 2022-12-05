package com.road801.android.data.network.dto

data class StoreDetailDto (
    val id: Int,
    val storeType: CodeDto,
    val name: String,
    val introduce: String,
    val phone: String,
    val address: String,
    val image: String?
)