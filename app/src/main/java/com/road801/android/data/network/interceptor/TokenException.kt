package com.road801.android.data.network.interceptor
import com.road801.android.data.network.error.NetworkError
import com.road801.android.data.network.error.NetworkException

class TokenException(val networkError: NetworkError, throwable: Throwable? = null) : NetworkException(networkError, throwable)