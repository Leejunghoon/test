package com.road801.android.data.repository

import android.net.Uri
import com.road801.android.common.Constants
import com.road801.android.common.Constants.API_VERSION
import com.road801.android.common.GlobalApplication
import com.road801.android.common.enum.LoginType
import com.road801.android.common.util.extension.asMultipart
import com.road801.android.data.network.api.Api
import com.road801.android.data.network.dto.*
import com.road801.android.data.network.dto.requset.*
import com.road801.android.data.network.dto.response.*
import com.road801.android.data.network.error.*
import com.road801.android.data.network.interceptor.TokenException
import com.road801.android.data.network.interceptor.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException

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


            // server response (response) error
            throwable is ConnectException -> {
                domainErrorMessage = DOMAIN_NETWORK_ERROR_MESSAGE
                domainErrorSubMessage = "서버가 응답하지 않습니다."
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

    // 이미 존재하는 회원 여부
    suspend fun isIdExist(id: String, loginType: LoginType): IsExistResponseDto {
        try {
            return if (loginType == LoginType.DEFAULT) api.isExistId(id)
            else api.isExistId(loginType.name, id)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 회원가입
    suspend fun signup(requestDto: SignupRequestDto): SuccessResponseDto {
        try {
            return if (requestDto.socialId.isNullOrEmpty()) api.signup(requestDto)
            else api.signupSns(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 휴대폰 인증 요청
    suspend fun phoneAuth(requestDto: PhoneAuthRequestDto): PhoneAuthResponseDto {
        try {
            return api.phoneAuth(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 휴대폰 인증 확인
    suspend fun phoneAuthConfirm(mobileNo: String, authValue: String): SuccessResponseDto {
        try {
            return api.phoneAuthConfirm(mobileNo, authValue)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 소셜 로그인
    suspend fun loginSns(type: LoginType, id: String): LoginResponseDto {
        try {
            return api.login(LoginSNSRequestDto(type.name, id))
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 로드801 로그인
    suspend fun loginRoad(id: String, pw: String): LoginResponseDto {
        try {
            return api.login(LoginRoadRequestDto(id, pw))
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 홈 정보
    suspend fun home(): HomeResponseDto {
        try {
            return api.home()
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 홈 이벤트 정보
    suspend fun homeEvent(): HomeEventResponseDto {
        try {
            return api.homeEvent()
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 포인트 적립 내역 조회
    suspend fun pointHistory(requestDto: PaginationDto): CommonListResponseDto<PointHistoryDto> {
        try {
            return api.pointHistory(requestDto.nextId,requestDto.size,requestDto.sort)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }


    // 소식 목록 조회
    suspend fun news(requestDto: PaginationDto): CommonListResponseDto<NewsDto> {
        try {
            return api.news(requestDto.nextId,requestDto.size,requestDto.sort)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 소식 상세 조회
    suspend fun newsDetail(boardId: Int): NewsDetailDto {
        try {
            return api.newsDetail(boardId)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 소식 목록 조회
    suspend fun store(): CommonListResponseDto<StoreDto> {
        try {
            return api.store()
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 소식 상세 조회
    suspend fun storeDetail(storeId: Int): StoreDetailDto {
        try {
            return api.storeDetail(storeId)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }


    // 이벤트 목록 조회
    suspend fun event(requestDto: PaginationDto): CommonListResponseDto<EventDto> {
        try {
            return api.event(requestDto.nextId,requestDto.size,requestDto.sort)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 이벤트 상세 조회
    suspend fun eventDetail(eventId: Int): EventDto {
        try {
            return api.eventDetail(eventId)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 이벤트 참여
    suspend fun eventEnter(eventId: Int): PointDto {
        try {
            return api.eventEnter(eventId)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 휴대폰 인증 요청 (로그인 상태)
    suspend fun phoneAuth(mobileNo: String): PhoneAuthResponseDto {
        try {
            return api.phoneAuth(mobileNo)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 휴대폰 인증 확인 및 변경
    suspend fun phoneAuthConfirm(requestDto: PhoneAuthRequestDto): SuccessResponseDto {
        try {
            return api.phoneAuthConfirm(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 비밀번호 변경 인증번호 요청
    suspend fun changePwAuth(mobileNo: String): PhoneAuthResponseDto {
        try {
            return api.changePasswordAuth(mobileNo)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 비밀번호 변경
    suspend fun changePassword(requestDto: ChangePasswordRequestDto): SuccessResponseDto {
        try {
            return api.changePassword(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    // 회원 탈퇴
    suspend fun withdrawal(requestDto: WithdrawalRequestDto): SuccessResponseDto {
        try {
            return api.withdrawal(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    //  푸쉬 ID 저장
    suspend fun deviceId(requestDto: DeviceIdRequestDto): SuccessResponseDto {
        try {
            return api.deviceId(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    //  알림 푸쉬 수락 여부
    suspend fun fcmNotification(requestDto: ActiveRequestDto): SuccessResponseDto {
        try {
            return api.fcmNotification(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    //  광고 푸쉬 수락 여부
    suspend fun fcmAd(requestDto: ActiveRequestDto): SuccessResponseDto {
        try {
            return api.fcmAd(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    //  내 정보
    suspend fun me(): MeDto {
        try {
            return api.me()
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    //  내 정보 수정
    suspend fun me(requestDto: MeRequestDto): MeDto {
        try {
            return api.me(requestDto)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }

    //  프로필 사진 등록 및 수정
    suspend fun uploadProfileImage(file: Uri): UploadFileResponseDto {
        try {
            return api.uploadProfileImage(file.asMultipart("image", GlobalApplication.instance.contentResolver)!!)
        } catch (exception: Exception) {
            throw toDomainException(exception)
        }
    }


}