package com.road801.android.data.network.interceptor

import com.road801.android.data.network.error.NetworkError
import com.road801.android.data.network.error.ServerResponseException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.net.UnknownHostException

object TokenInterceptor : Interceptor {

    private const val TAG = "TokenInterceptor"
    private const val HEADER_AUTHORIZATION_KEY = "Authorization"
    private const val HEADER_X_REQUEST_TO = "x-request-to"
    private const val HEADER_X_REQUEST_TO_VALUE = "road801-auth-mobile-aljskkjdfinwnksid"
    private const val TOKEN_ERROR_UNKNOWN_HOST_EXCEPTION = "서버 접속에 오류가 발생하였습니다. 원인: UnknownHostException"

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val request: Request = chain.request()
            val authenticatedRequest: Request = when {
                findBearerTokenAnnotation(request) != null -> {
                    request.newBuilder()
//                        .addHeader(HEADER_AUTHORIZATION_KEY, "Bearer ${TokenDatabase.fetchAccessToken()}")
                        .addHeader(HEADER_AUTHORIZATION_KEY, "${LocalDatabase.fetchAccessToken()}")
                        .addHeader(HEADER_X_REQUEST_TO, HEADER_X_REQUEST_TO_VALUE)
                        .build()
                }
                else -> {
                    request.newBuilder()
                        .addHeader(HEADER_X_REQUEST_TO, HEADER_X_REQUEST_TO_VALUE)
                        .build()
                }
            }
            chain.proceed(authenticatedRequest)
        } catch (exception: Exception) {
            when (exception) {
                is UnknownHostException -> {
                    throw TokenException(NetworkError("UnknownHostException", TOKEN_ERROR_UNKNOWN_HOST_EXCEPTION))
                }
                is ServerResponseException -> {
                    throw exception
                }
                else -> {
                    throw exception
                }
            }
        }
    }

    private fun findBearerTokenAnnotation(request: Request): BearerToken? {
        return request.tag(Invocation::class.java)
            ?.method()
            ?.annotations
            ?.filterIsInstance<BearerToken>()
            ?.firstOrNull()
    }
}