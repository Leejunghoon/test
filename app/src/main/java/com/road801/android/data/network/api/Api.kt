package com.road801.android.data.network.api

import com.road801.android.data.network.dto.*
import com.road801.android.data.network.dto.requset.*
import com.road801.android.data.network.dto.response.*
import com.road801.android.data.network.interceptor.BearerToken
import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {

    // MARK - 회원가입 -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * SNS 이미 존재하는 회원인지 여부
     *
     * @param socialType KAKAO, NAVER, GOOGLE
     * @param id
     * @return IsExistResponseDto
     */
    @GET("customer/signup/duplId/{socialType}/{socialId}")
    suspend fun isExistId(
        @Path("socialType") socialType: String,
        @Path("socialId") id: String
    ): IsExistResponseDto

    /**
     * 로드801 이미 존재하는 회원인지 여부
     *
     * @param id
     * @return IsExistResponseDto
     */
    @GET("customer/signup/duplId/{loginId}")
    suspend fun isExistId(@Path("loginId") id: String): IsExistResponseDto

    /**
     * 회원가입 소셜
     *
     * @param params SignupRequestDto
     * @return SuccessResponseDto
     */
    @POST("customer/signup/social")
    suspend fun signupSns(@Body request: SignupRequestDto): SuccessResponseDto

    /**
     * 회원가입 로드801
     *
     * @param params SignupRequestDto
     * @return SuccessResponseDto
     */
    @POST("customer/signup/default")
    suspend fun signup(@Body request: SignupRequestDto): SuccessResponseDto

    /**
     * 휴대폰 인증 요청 (회원가입)
     *
     * @param params PhoneAuthRequestDto
     * @return PhoneAuthResponseDto
     */
    @POST("customer/signup/auth/phone")
    suspend fun phoneAuth(@Body params: PhoneAuthRequestDto): PhoneAuthResponseDto

    /**
     *  휴대폰 인증 확인 (회원가입)
     *
     * @param mobileNo 휴대폰 번호
     * @param authValue 인증번호
     * @return SuccessResponseDto
     */
    @GET("customer/signup/auth/phone")
    suspend fun phoneAuthConfirm(
        @Query("mobileNo") mobileNo: String,
        @Query("authValue") authValue: String,
    ): SuccessResponseDto


    // MARK - 로그인 -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 로그인 (소셜)
     *
     * @param params LoginSNSRequestDto
     * @return PhoneAuthResponseDto
     */
    @POST("customer/login/social")
    suspend fun login(@Body params: LoginSNSRequestDto): LoginResponseDto

    /**
     * 로그인 (로드801)
     *
     * @param params LoginRoadRequestDto
     * @return PhoneAuthResponseDto
     */
    @POST("customer/login/default")
    suspend fun login(@Body params: LoginRoadRequestDto): LoginResponseDto

    /**
     * 비밀번호 변경
     * 인증번호 요청
     *
     * @param mobileNo mobileNo
     * @return PhoneAuthResponseDto
     */
    @POST("customer/login/password/{mobileNo}")
    suspend fun changePasswordAuth(@Path("mobileNo") mobileNo: String): PhoneAuthResponseDto

    /**
     *  비밀번호 번호 변경
     *  인증번호 확인 및 변경
     *
     * @param params PhoneAuthRequestDto
     * @return SuccessResponseDto
     */
    @PATCH("customer/login/password/")
    suspend fun changePassword(@Body params: ChangePasswordRequestDto): SuccessResponseDto


    // MARK - HOME --------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    @BearerToken
    @GET("customer/home")
    suspend fun home(): HomeResponseDto

    @BearerToken
    @GET("customer/home/event")
    suspend fun homeEvent(): HomeEventResponseDto


    // MARK - Point -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 포인트 적립 내역 조회
     *
     * @param params PaginationDto
     * @return CommonListResponseDto<PointHistoryDto>
     */
    @BearerToken
    @GET("customer/point/earn")
    suspend fun pointHistory(@Query("nextId") nextId: String?, @Query("size") size: Int, @Query("sort") sort: List<String>): CommonListResponseDto<PointHistoryDto>

    // MARK - ME -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 휴대폰 번호 변경
     * 인증번호 요청 (로그인 상태)
     *
     * @param mobileNo
     * @return PhoneAuthResponseDto
     */
    @BearerToken
    @POST("customer/me/auth/{mobileNo}")
    suspend fun phoneAuth(@Path("mobileNo") mobileNo: String): PhoneAuthResponseDto

    /**
     *  휴대폰 번호 변경
     *  인증번호 확인 및 변경 (로그인 상태)
     *
     * @param params PhoneAuthRequestDto
     * @return SuccessResponseDto
     */
    @BearerToken
    @PATCH("customer/me/mobileNo")
    suspend fun phoneAuthConfirm(@Body params: PhoneAuthRequestDto): SuccessResponseDto

    /**
     * 회원 탈퇴
     *
     * @return SuccessResponseDto
     */
    @BearerToken
    @POST("customer/me/drop")
    suspend fun withdrawal(@Body params: WithdrawalRequestDto): SuccessResponseDto

    /**
     * 토큰 갱신
     *
     * @return LoginResponseDto
     */
    @BearerToken
    @POST("customer/me/token")
    suspend fun refreshToken(): LoginResponseDto


    /**
     * 푸쉬 ID 저장
     *
     * @param params DeviceIdRequestDto
     * @return SuccessResponseDto
     */
    @BearerToken
    @POST("customer/me/device")
    suspend fun deviceId(@Body params: DeviceIdRequestDto): SuccessResponseDto

    /**
     * 알림 푸쉬 수락 여부
     *
     * @param isActive
     * @return SuccessResponseDto
     */
    @BearerToken
    @PATCH("customer/me/push/noti")
    suspend fun fcmNotification(@Body params: ActiveRequestDto): SuccessResponseDto


    /**
     * 광고 푸쉬 수락 여부
     *
     * @param isActive
     * @return SuccessResponseDto
     */
    @BearerToken
    @PATCH("customer/me/push/ad")
    suspend fun fcmAd(@Body params: ActiveRequestDto): SuccessResponseDto

    /**
     * 내 정보
     *
     * @return MeDto
     */
    @BearerToken
    @GET("customer/me")
    suspend fun me(): MeDto

    /**
     * 내 정보 수정
     *
     * @param params MeRequestDto
     * @return MeDto
     */
    @BearerToken
    @PATCH("customer/me")
    suspend fun me(@Body params: MeRequestDto): MeDto

    /**
     * 프로필 사진 업로드 및 수정
     *
     * @param image
     * @return UploadFileResponseDto
     */
    @BearerToken
    @Multipart
    @POST("customer/me/profileImage")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): UploadFileResponseDto




    // MARK - News -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 소식 목록 조회
     *
     * @param params PaginationDto
     * @return CommonListResponseDto<NewsDto>
     */
    @BearerToken
    @GET("customer/board")
    suspend fun news(@Query("nextId") nextId: String?, @Query("size") size: Int, @Query("sort") sort: List<String>): CommonListResponseDto<NewsDto>

    /**
     * 소식 상세 조회
     *
     * @param boardId
     * @return NewsDto
     */
    @BearerToken
    @GET("customer/board/{boardId}")
    suspend fun newsDetail(@Path("boardId") boardId: Int): NewsDetailDto


    /**
     * 매장 목록 조회
     *
     * @param params PaginationDto
     * @return CommonListResponseDto<StoreDto>
     */
    @BearerToken
    @GET("customer/store")
    suspend fun store(): CommonListResponseDto<StoreDto>

    /**
     * 매장 상세 조회
     *
     * @param storeId
     * @return StoreDetailDto
     */
    @BearerToken
    @GET("customer/store/{storeId}")
    suspend fun storeDetail(@Path("storeId") storeId: Int): StoreDetailDto


    // MARK - Event -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 이벤트 목록 조회
     *
     * @param params PaginationDto
     * @return CommonListResponseDto<EventDto>
     */
    @BearerToken
    @GET("customer/event")
    suspend fun event(@Query("nextId") nextId: String?, @Query("size") size: Int, @Query("sort") sort: List<String>): CommonListResponseDto<EventDto>


    /**
     * 이벤트 상세 조회
     *
     * @param eventId
     * @return EventDto
     */
    @BearerToken
    @GET("customer/event/{eventId}")
    suspend fun eventDetail(@Path("eventId") eventId: Int): EventDto


    /**
     * 이벤트 참여 (포인트 획득 등)
     *
     * @param eventId
     * @return RemainPointResponseDto
     */
    @BearerToken
    @POST("customer/event/{eventId}/receive")
    suspend fun eventEnter(@Path("eventId") eventId: Int): PointDto


    // MARK - Alert -------------------------------------------------- -------------------------------------------------- --------------------------------------------------
    /**
     * 알림 목록 조회
     *
     * @param params PaginationDto
     * @return CommonListResponseDto<AlertDto>
     */
    @BearerToken
    @GET("customer/alert")
    suspend fun alert(@Query("nextId") nextId: String?, @Query("size") size: Int): CommonListResponseDto<AlertDto>

}
