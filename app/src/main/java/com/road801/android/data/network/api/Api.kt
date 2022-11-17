package com.road801.android.data.network.api

import com.road801.android.data.network.dto.*
import com.road801.android.data.network.dto.requset.LoginRoadRequestDto
import com.road801.android.data.network.dto.requset.LoginSNSRequestDto
import com.road801.android.data.network.dto.requset.PhoneAuthRequestDto
import com.road801.android.data.network.dto.requset.SignupRequestDto
import com.road801.android.data.network.dto.response.*
import com.road801.android.data.network.interceptor.BearerToken
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
     * 핸드폰 인증 요청 (회원가입)
     *
     * @param params PhoneAuthRequestDto
     * @return PhoneAuthResponseDto
     */
    @POST("customer/signup/auth/phone")
    suspend fun phoneAuth(@Body params: PhoneAuthRequestDto): PhoneAuthResponseDto

    /**
     *  핸드폰 인증 확인 (회원가입)
     *
     * @param mobileNo 핸드폰 번호
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
    suspend fun pointHistory(@Query("pageable") params: PaginationDto): CommonListResponseDto<PointHistoryDto>


    // MARK - ME -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 핸드폰 인증 요청 (로그인)
     *
     * @param mobileNo
     * @return PhoneAuthResponseDto
     */
    @BearerToken
    @POST("customer/me/auth/{mobileNo}")
    suspend fun phoneAuth(@Path("mobileNo") mobileNo: String): PhoneAuthResponseDto

    /**
     *  핸드폰 인증 확인 및 변경 (로그인)
     *
     * @param params PhoneAuthRequestDto
     * @return SuccessResponseDto
     */
    @BearerToken
    @PATCH("customer/me/mobileNo")
    suspend fun phoneAuthConfirm(@Body params: PhoneAuthRequestDto): SuccessResponseDto




    // MARK - News -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 소식 목록 조회
     *
     * @param params PaginationDto
     * @return CommonListResponseDto<NewsDto>
     */
    @BearerToken
    @GET("customer/board")
    suspend fun news(@Query("pageable") params: PaginationDto): CommonListResponseDto<NewsDto>

    /**
     * 소식 상세 조회
     *
     * @param boardId
     * @return NewsDto
     */
    @BearerToken
    @GET("customer/board/{boardId}")
    suspend fun newsDetail(@Path("boardId") boardId: Int): NewsDetailDto




    // MARK - Event -------------------------------------------------- -------------------------------------------------- --------------------------------------------------

    /**
     * 이벤트 목록 조회
     *
     * @param params PaginationDto
     * @return CommonListResponseDto<EventDto>
     */
    @BearerToken
    @GET("customer/event")
    suspend fun event(@Query("pageable") params: PaginationDto): CommonListResponseDto<EventDto>


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
}
