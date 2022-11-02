package com.road801.android.data.network.error

internal class ServerResponseException(val networkError: NetworkError, throwable: Throwable? = null) : NetworkException(networkError, throwable)