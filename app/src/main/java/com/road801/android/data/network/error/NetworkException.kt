package com.road801.android.data.network.error

import java.io.IOException

abstract class NetworkException(private val networkError: NetworkError, private val throwable: Throwable? = null) : IOException(throwable) {


    final override val message: String
        get() = throwable?.message ?: DOMAIN_UNDEFINED_ERROR_SUB_MESSAGE

    final override fun getLocalizedMessage(): String {
        return throwable?.message ?: DOMAIN_UNDEFINED_ERROR_SUB_MESSAGE
    }

    final override fun toString(): String {
        return "NetworkError: $networkError {\n" +
                "   errorCode: ${networkError.errorCode}\n" +
                "   errorMessage: ${networkError.errorMessage}\n" +
                "   Cause: ${super.cause}" +
                "}"
    }
}