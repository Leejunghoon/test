package com.road801.android.domain.transfer

import java.io.IOException

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val exception: DomainException) : Resource<Nothing>()
}

class DomainException(val domainErrorMessage: String = DOMAIN_UNDEFINED_ERROR_MESSAGE, val domainErrorSubMessage: String = DOMAIN_UNDEFINED_ERROR_SUB_MESSAGE, cause: Throwable? = null) : IOException(cause)

const val DOMAIN_NETWORK_ERROR_MESSAGE = "네트워크 오류"
const val DOMAIN_SERVER_AUTH_ERROR_MESSAGE = "서버 인증 오류"
const val DOMAIN_SERVER_ERROR_MESSAGE = "알림"
const val DOMAIN_UNDEFINED_ERROR_MESSAGE = "오류"
const val DOMAIN_UNDEFINED_ERROR_SUB_MESSAGE = "알 수 없는 오류가 발생하였습니다."

const val DOMAIN_SERVICE_ERROR = ""