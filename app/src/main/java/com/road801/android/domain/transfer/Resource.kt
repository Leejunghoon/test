package com.road801.android.domain.transfer

import com.google.android.gms.common.api.ApiException
import com.road801.android.data.network.error.DomainException
import java.io.IOException

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val exception: DomainException) : Resource<Nothing>()
}
