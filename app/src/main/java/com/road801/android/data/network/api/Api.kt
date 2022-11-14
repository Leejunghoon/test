package com.road801.android.data.network.api

import com.road801.android.data.network.dto.requset.LoginRoadRequestDto
import com.road801.android.data.network.dto.requset.LoginSNSRequestDto
import com.road801.android.data.network.dto.requset.PhoneAuthRequestDto
import com.road801.android.data.network.dto.requset.SignupRequestDto
import com.road801.android.data.network.dto.response.IsExistResponseDto
import com.road801.android.data.network.dto.response.LoginResponseDto
import com.road801.android.data.network.dto.response.PhoneAuthResponseDto
import com.road801.android.data.network.dto.response.SuccessResponseDto
import retrofit2.http.*

interface Api {

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

//    @GET( "customer/signup/duplId")
//    suspend fun isdxistId(
//        @Query("loginId") id: String,
//    ): IsExistResponseDto
//

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
     * 핸드폰 인증 요청 (로그인)
     *
     * @param mobileNo
     * @return PhoneAuthResponseDto
     */
    @POST("customer/me/auth/{mobileNo}")
    suspend fun phoneAuth(@Path("mobileNo") mobileNo: String): PhoneAuthResponseDto

    /**
     *  핸드폰 인증 확인 및 변경 (로그인)
     *
     * @param params PhoneAuthRequestDto
     * @return SuccessResponseDto
     */
    @PATCH("customer/me/mobileNo")
    suspend fun phoneAuthConfirm(@Body params: PhoneAuthRequestDto): SuccessResponseDto

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


}