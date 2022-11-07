package com.road801.android.data.repository

import com.road801.android.common.Constants
import com.road801.android.common.Constants.API_VERSION
import com.road801.android.common.enum.SnsType
import com.road801.android.data.network.api.Api
import com.road801.android.data.network.dto.IsExistResponseDto
import com.road801.android.data.network.error.*
import com.road801.android.data.network.interceptor.TokenException
import com.road801.android.data.network.interceptor.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerRepository {

    private val api = createApiClient()

    private fun createApiClient(): Api {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor)
            .addInterceptor(ServerResponseInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL + API_VERSION)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }


    private fun toDomainException(throwable: Throwable): DomainException {
        var domainErrorMessage = DOMAIN_UNDEFINED_ERROR_MESSAGE
        var domainErrorSubMessage = DOMAIN_UNDEFINED_ERROR_SUB_MESSAGE

        when {
            // network error
            throwable is HttpException -> {
                val responseBody = throwable.response()?.errorBody()
                val responseJson = JSONObject(responseBody!!.string())
                domainErrorMessage = DOMAIN_NETWORK_ERROR_MESSAGE
                domainErrorSubMessage = responseJson.toString()
            }

            // server response (token) error
            throwable is TokenException -> {
                domainErrorMessage = DOMAIN_SERVER_AUTH_ERROR_MESSAGE
                domainErrorSubMessage = throwable.networkError.errorMessage.toString()
            }

            // server response (response) error
            throwable is ServerResponseException -> {
                domainErrorMessage = DOMAIN_SERVER_ERROR_MESSAGE
                domainErrorSubMessage = throwable.networkError.errorMessage.toString()
//                if (throwable.networkError.errorCode == "E20002") { // TODO: refactoring - extract enum class
//                    logout()
//                    GlobalApplication.instance.goToIntroActivity()
//                }
            }

            // 그 외 Domain 레이어 이하에서 발생하는 예외 상황들
            throwable.message != null -> {
                domainErrorMessage = DOMAIN_UNDEFINED_ERROR_MESSAGE
                domainErrorSubMessage = throwable.message ?: DOMAIN_UNDEFINED_ERROR_SUB_MESSAGE
            }
        }

        return DomainException(domainErrorMessage, domainErrorSubMessage, throwable)
    }


    suspend fun isIdExist(id: String, snsType: SnsType? = null): IsExistResponseDto {
        try {
            return if (snsType == null) api.isExistId(id)
            else api.isExistId(snsType.name, id)
        } catch (exception: Exception) {

            throw toDomainException(exception)
        }
    }
}