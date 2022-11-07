package com.road801.android.data.network.api

import com.road801.android.data.network.dto.IsExistResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    /**
     * 소셜 로그인 아이디
     *
     * @param socialType KAKAO, NAVER, GOOGLE
     * @param id
     * @return IsExistResponseDto
     */
    @GET( "customer/signup/duplId/{socialType}/{socialId}")
    suspend fun isExistId(
        @Path("socialType") socialType: String,
        @Path("socialId") id: String
    ): IsExistResponseDto

    /**
     * 로드801 로그인 아이디
     *
     * @param id
     * @return IsExistResponseDto
     */
    @GET( "customer/signup/duplId/{loginId}")
    suspend fun isExistId(
        @Path("loginId") id: String,
    ): IsExistResponseDto

//    @GET( "customer/signup/duplId")
//    suspend fun isdxistId(
//        @Query("loginId") id: String,
//    ): IsExistResponseDto
//

//    /**
//     * CI 기준 가입 유무 확인
//     */
//    @POST(Constants.API_VERSION + "customer/signup/exist")
//    suspend fun isExistCI(@Body isExistCIRequestDto: IsExistCIRequestDto): IsExistResponseDto

}