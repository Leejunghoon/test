package com.road801.android.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.road801.android.BuildConfig
import com.road801.android.common.TAG
import com.road801.android.common.enum.GenderType
import com.road801.android.common.enum.SnsType
import com.road801.android.data.network.dto.UserDto
import com.road801.android.domain.transfer.DomainException
import com.road801.android.domain.transfer.Resource

object SnsRepository {
    // for kakao
    private lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit

    // for naver
    private lateinit var oAuthLoginCallback: OAuthLoginCallback
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    // for google
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleResultLauncher:ActivityResultLauncher<Intent>

    /**
     * MARK: - 카카오 로그인
     *
     * @param context
     * @param callback UserDTO
     */
    public fun kakaoLogin(context: Context, callback: (result: Resource<UserDto>) -> Unit) {
        setupKakaoCallback(context, callback)

        // process login
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = kakaoCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
        }
    }

    /**
     * MARK: - 네이버 로그인
     *
     * @param context
     * @return
     */
    public fun naverLogin(context: Context, callback: (result: Resource<UserDto>) -> Unit) {
        setupOauthLoginCallback(callback)
        NaverIdLoginSDK.authenticate(context, oAuthLoginCallback)
    }

    /**
     * MARK: - 구글 로그인
     *
     * @param context
     * @param resultLauncher 콜백
     */
    public fun googleLogin(context: Context, resultLauncher: ActivityResultLauncher<Intent>) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestId()
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleResultLauncher = resultLauncher
        googleResultLauncher.launch(googleSignInClient.signInIntent)
    }

    /**
     *  SNS 로그아웃
     *
     * @param type KAKAO, NAVER, GOOGLE
     * @param callback none: success
     */
    public fun logout(type: SnsType, callback: () -> Unit) {
        when (type) {
            SnsType.KAKAO -> {
                UserApiClient.instance.logout {
                    if(BuildConfig.DEBUG) Log.d(TAG, "[카카오] 로그아웃 성공")
                    callback.invoke()
                }
            }
            SnsType.NAVER -> {
                if(BuildConfig.DEBUG) Log.d(TAG, "[네이버] 로그아웃 성공")
                NaverIdLoginSDK.logout()
                callback.invoke()
            }

            SnsType.GOOGLE -> {
                if(BuildConfig.DEBUG) Log.d(TAG, "[구글] 로그아웃 성공")
                googleSignInClient.signOut()
            }
        }
    }

    /**
     * 회원 탈퇴시 SNS 연동 해제
     *
     * @param context
     * @param type KAKAO, NAVER, GOOGLE
     * @param callback Boolean: success, fail
     */
    public fun withdrawal(context: Context, type: SnsType, callback: (result: Boolean) -> Unit) {
        when (type) {
            SnsType.KAKAO -> {
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 연동 해제 실패", error)
                        callback.invoke(false)
                    }
                    else {
                        if(BuildConfig.DEBUG) Log.d(TAG, "[카카오] 연동 해제 성공")
                        callback.invoke(true)
                    }
                }
            }

            SnsType.NAVER -> {
                NidOAuthLogin().callDeleteTokenApi(context, object : OAuthLoginCallback {
                    override fun onSuccess() {
                        //서버에서 토큰 삭제에 성공한 상태입니다.
                        if(BuildConfig.DEBUG) Log.d(TAG, "[네이버] 연동 해제 성공")
                        callback.invoke(true)
                    }
                    override fun onFailure(httpStatus: Int, message: String) {
                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                        if (BuildConfig.DEBUG) Log.d(TAG, "$errorCode $errorDescription")
                        if(BuildConfig.DEBUG) Log.d(TAG, "[네이버] 연동 해제 성공")
                        callback.invoke(true)
                    }
                    override fun onError(errorCode: Int, message: String) {
                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                        onFailure(errorCode, message)
                    }
                })
            }

            SnsType.GOOGLE -> {
                if(BuildConfig.DEBUG) Log.d(TAG, "[구글] 로그아웃 성공")
                googleSignInClient.signOut()
            }
        }
    }

    public fun getHashKey(context: Context): String {
        return Utility.getKeyHash(context);
    }

    /**
     * 카카오 사용자 정보 가져오기.
     *
     * @param callback UserDto
     */
    private fun requestKaKaoUserInformation(callback: (result: Resource<UserDto>) -> Unit) {
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                if(BuildConfig.DEBUG) Log.d(
                    TAG,
                    "카카오 사용자 정보 요청 성공" + "\n회원번호: ${user.id}"
                            + "\n이름: ${user.kakaoAccount?.name}"
                            + "\n이메일: ${user.kakaoAccount?.email}"
                            + "\n닉네임: ${user.kakaoAccount?.profile?.nickname}"
                            + "\n생일: ${user.kakaoAccount?.birthday}"
                            + "\n성별: ${user.kakaoAccount?.gender}"
                            + "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                )

                user.kakaoAccount?.let {
                    val userId = user.id!!.toString()
                    val userName = it.name
                    val birthday = it.birthday
                    val gender = it.gender
                    val snsType = SnsType.KAKAO.name
                    val thumbnailImageUrl = it.profile?.thumbnailImageUrl

                    val userDto = UserDto(
                        name = userName,
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
                if(BuildConfig.DEBUG) Log.e(TAG, "requestKaKaoUserInformation: $error")
                callback.invoke(
                    Resource.Failure(
                        DomainException(
                            "[카카오] 사용자 정보 요청 실패.",
                            "카카오 사용자 정보를 가져오는데 실패하였습니다.",
                            error
                        )
                    )
                );
            }
        }
    }

    /**
     * 카카오 로그인 콜백 (추상화)
     *
     * @param context
     * @param callback UserDto
     */
    private fun setupKakaoCallback(context: Context, callback: (result: Resource<UserDto>) -> Unit) {
        kakaoCallback = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 접근이 거부 됨(동의 취소)")
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 유효하지 않은 앱")
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 요청 파라미터 오류")
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 유효하지 않은 scope ID")
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 서버 내부 에러")
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 앱이 요청 권한이 없음")
                    }
                    else -> { // Unknown
                        if(BuildConfig.DEBUG) Log.e(TAG, "[카카오] 기타 에러 " + error.toString())
                    }
                }

                if (error.toString().contains("statusCode=302")) {
                    // 카카오톡은 설치되어있지만 로그인이 되어있지 않은 상태라면 웹창으로 로그인을 유도해아함.
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
                } else {
                    callback.invoke(
                        Resource.Failure(
                            DomainException(
                                "[카카오] 로그인 실패",
                                "카카오 로그인을 취소하였습니다.",
                                error
                            )
                        )
                    );
                }

            } else if (token != null) {
                if(BuildConfig.DEBUG) Log.d(TAG, "[카카오] 로그인 성공")

                requestKaKaoUserInformation(callback)
            }
        }
    }



    /**
     * 네이버 인증 콜백.
     *
     */
    private fun setupOauthLoginCallback(callback: (result: Resource<UserDto>) -> Unit) {
        oAuthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                if (BuildConfig.DEBUG) Log.d(TAG, "[네이버] 로그인 인증 성공.")
                requestNaverProfile(callback)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                if (BuildConfig.DEBUG) Log.d(TAG, "$errorCode $errorDescription")

                callback.invoke(Resource.Failure(DomainException(
                    "[네이버] 로그인 인증 실패.",
                    "$errorCode $errorDescription")
                ))
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }
    }


    /**
     * 네이버 사용자 정보 가져오기.
     *
     */
    private fun requestNaverProfile(callback: (result: Resource<UserDto>) -> Unit) {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                if (BuildConfig.DEBUG) Log.d(TAG, "$response")

                response.profile?.let {
                    val userId = it.id
                    val userName = it.name
                    val birthday = it.birthday
                    val gender = it.gender
                    val snsType = SnsType.NAVER.name
                    val thumbnailImageUrl = it.profileImage

                    val userDto = UserDto(
                        name = userName,
                        birthday = birthday,
                        mobileNo = "",
                        sexType = if (gender == null) GenderType.NONE
                        else GenderType.valueOf( if (gender == "M") "MALE" else "FEMALE"),
                        termAgreeList = emptyList(),
                        socialType = snsType,
                        socialId = userId,
                        loginId = null,
                        password = null,
                        thumbnailImageUrl = thumbnailImageUrl
                    )

                    callback.invoke(Resource.Success(userDto))
                }

            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                if (BuildConfig.DEBUG) Log.d(TAG, "$errorCode $errorDescription")
                callback.invoke(Resource.Failure(DomainException(
                    "[네이버] 사용자 정보 요청 실패.",
                    "$errorCode $errorDescription")
                ))
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }


}