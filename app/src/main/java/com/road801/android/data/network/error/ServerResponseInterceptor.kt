package com.road801.android.data.network.error

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.road801.android.data.network.dto.ErrorResponseDto
import okhttp3.Interceptor

import okhttp3.Request
import okhttp3.Response

class ServerResponseInterceptor : Interceptor {

    private val tag = "ResponseInterceptor"
    private val gson = GsonBuilder().create()


    @Throws(ServerResponseException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d(tag, "Response intercept..")
        val request: Request = chain.request()
        val response = chain.proceed(request)
        if (response.isSuccessful.not()) {
            val jsonResponse: String?
            val errorResponseDto: ErrorResponseDto?
            try {
                errorResponseDto = gson.fromJson(response.body?.charStream(), ErrorResponseDto::class.java)
                jsonResponse = errorResponseDto.toString()
            } catch (jsonSyntaxException: JsonSyntaxException) {
                Log.d(tag, "JsonSyntaxException:")
                throw ServerResponseException(NetworkError("JsonSyntaxException", SERVER_RESPONSE_JSON_PARSE_ERROR), jsonSyntaxException)
            } catch (throwable: Throwable) {
                Log.d(tag, "Throwable:")
                throw ServerResponseException(NetworkError("Throwable", SERVER_RESPONSE_UNKNOWN_ERROR), throwable)
            }
            throw ServerResponseException(NetworkError(errorResponseDto.code, errorResponseDto.message), Throwable(jsonResponse))
        }
        return response
    }


    companion object {
        const val SERVER_RESPONSE_JSON_PARSE_ERROR = "서버응답에 오류가 있습니다 원인: JsonSyntaxException"
        const val SERVER_RESPONSE_UNKNOWN_ERROR = "알 수 없는 오류가 발생하였습니다"
    }
}