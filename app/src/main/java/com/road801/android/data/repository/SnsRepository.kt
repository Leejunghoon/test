package com.road801.android.data.repository

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.road801.android.common.enum.GenderType
import com.road801.android.common.enum.SnsType
import com.road801.android.data.network.dto.UserDto
import com.road801.android.domain.transfer.DomainException
import com.road801.android.domain.transfer.Resource

object SnsRepository {
    private final val TAG_KAKAO = "KAKAO"
    private lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit


    public fun kakaoLogin(context: Context, callback: (result: Resource<UserDto>) -> Unit) {
        setupKakaoCallback(context, callback)

        // process login
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = kakaoCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
        }
    }

    // 카카오 사용자 정보 가져오기.
    private fun requestKaKaoUserInformation(callback: (result: Resource<UserDto>) -> Unit) {
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                Log.d(
                    TAG_KAKAO,
                    "카카오 사용자 정보 요청 성공" + "\n회원번호: ${user.id}"
                            + "\n이메일: ${user.kakaoAccount?.email}"
                            + "\n닉네임: ${user.kakaoAccount?.profile?.nickname}"
                            + "\n생일: ${user.kakaoAccount?.birthday}"
                            + "\n성별: ${user.kakaoAccount?.gender}"
                            + "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                )

                user.kakaoAccount?.let {
                    val userId = user.id!!
                    val birthday = it.birthday
                    val gender = it.gender
                    val snsType = SnsType.KAKAO.name
                    val thumbnailImageUrl = it.profile?.thumbnailImageUrl

                    val userDto = UserDto(
                        name = "",
                        birthday = birthday,
                        mobileNo = "",
                        sexType = if (gender == null) GenderType.NONE else GenderType.valueOf(
                            gender.name
                        ),
                        termAgreeList = emptyList(),
                        socialType = snsType,
                        socialId = userId,
                        loginId = null,
                        password = null,
                        thumbnailImageUrl = thumbnailImageUrl
                    )
                    callback.invoke(Resource.Success(userDto))
                }

            } else {
                Log.d(TAG_KAKAO, "requestKaKaoUserInformation: $error")
                callback.invoke(
                    Resource.Failure(
                        DomainException(
                            "카카오 로그인을 다시 시도해주세요.",
                            "카카오 사용자 정보를 가져오는데 실패하였습니다.",
                            error
                        )
                    )
                );
            }
        }
    }


    private fun setupKakaoCallback(context: Context, callback: (result: Resource<UserDto>) -> Unit) {
        kakaoCallback = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Log.e(TAG_KAKAO, "접근이 거부 됨(동의 취소)")
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Log.e(TAG_KAKAO, "유효하지 않은 앱")
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Log.e(TAG_KAKAO, "인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Log.e(TAG_KAKAO, "요청 파라미터 오류")
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Log.e(TAG_KAKAO, "유효하지 않은 scope ID")
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Log.e(TAG_KAKAO, "설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Log.e(TAG_KAKAO, "서버 내부 에러")
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Log.e(TAG_KAKAO, "앱이 요청 권한이 없음")
                    }
                    else -> { // Unknown
                        Log.e(TAG_KAKAO, "기타 에러 " + error.toString())
                    }
                }

                if (error.toString().contains("statusCode=302")) {
                    // 카카오톡은 설치되어있지만 로그인이 되어있지 않은 상태라면 웹창으로 로그인을 유도해아함.
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
                } else {
                    callback.invoke(
                        Resource.Failure(
                            DomainException(
                                "로그인 실패",
                                "카카오 로그인을 취소하였습니다.",
                                error
                            )
                        )
                    );
                }

            } else if (token != null) {
                Log.d(TAG_KAKAO, "로그인 성공")

                requestKaKaoUserInformation(callback)
            }
        }
    }

    public fun getHashKey(): String {
        return "";
//        return Utility.getKeyHash(this);
    }

}