package com.road801.android.domain.transfer

import com.road801.android.data.network.error.DomainException

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val exception: DomainException) : Resource<Nothing>()
}
