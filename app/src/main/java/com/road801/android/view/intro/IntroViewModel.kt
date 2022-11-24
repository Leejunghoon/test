package com.road801.android.view.intro

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.road801.android.BuildConfig
import com.road801.android.common.enum.LoginType
import com.road801.android.common.util.extension.TAG
import com.road801.android.data.network.dto.UserDto
import com.road801.android.data.network.dto.requset.ChangePasswordRequestDto
import com.road801.android.data.network.dto.requset.PhoneAuthRequestDto
import com.road801.android.data.network.dto.requset.SignupRequestDto
import com.road801.android.data.network.dto.response.LoginResponseDto
import com.road801.android.data.network.error.DomainException
import com.road801.android.data.network.interceptor.LocalDatabase
import com.road801.android.data.repository.ServerRepository
import com.road801.android.data.repository.SnsRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor() : ViewModel() {

    // 회원가입 프로세스 데이터 (공통)
    private var _signupUser = MutableLiveData<Event<Resource<UserDto>>>()
    val signupUser: LiveData<Event<Resource<UserDto>>> = _signupUser

    // 이미 가입한 회원 존재 유무
    private var _isMemberExist = MutableLiveData<Event<Resource<Boolean>>>()
    val isMemberExist: LiveData<Event<Resource<Boolean>>> = _isMemberExist

    // 휴대폰 인증 요청 여부 (회원가입)
    private var _isRequestCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isRequestCert: LiveData<Event<Resource<Boolean>>> = _isRequestCert

    // 휴대폰 인증 완료 여부 (회원가입)
    private var _isCompleteCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isCompleteCert: LiveData<Event<Resource<Boolean>>> = _isCompleteCert

    // 회원가입 성공 여부
    private var _isSuccessSignup = MutableLiveData<Event<Resource<Boolean>>>()
    val isSuccessSignup: LiveData<Event<Resource<Boolean>>> = _isSuccessSignup

    // 로그인 성공 여부
    private var _isSuccessLogin = MutableLiveData<Event<Resource<LoginResponseDto>>>()
    val isSuccessLogin: LiveData<Event<Resource<LoginResponseDto>>> = _isSuccessLogin

    // 휴대폰 인증 요청 여부 (비밀번호 변경)
    private var _isRequestPwCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isRequestPwCert: LiveData<Event<Resource<Boolean>>> = _isRequestPwCert

    // 휴대폰 인증 완료 여부 (비밀번호 변경)
    private var _isCompletePwCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isCompletePwCert: LiveData<Event<Resource<Boolean>>> = _isCompletePwCert



    /**
     * 회원가입
     *
     * @param request
     */
    public fun requestSignup(request: SignupRequestDto) {
        viewModelScope.launch {
            try {
                _isSuccessSignup.value = Event(Resource.Loading)
                ServerRepository.signup(request)
                _isSuccessSignup.value = Event(Resource.Success(true))
            } catch (domainException: DomainException) {
                _isSuccessSignup.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isSuccessSignup.value = Event(Resource.Failure(DomainException(domainErrorSubMessage = "회원가입에 실패하였습니다.", cause = exception)))
            }
        }
    }

    /**
     * SNS ID로 이미 가입된 회원 여부
     *
     * @param id
     * @param loginType
     */
    public fun requestIsIdExist(id: String, loginType: LoginType) {
        viewModelScope.launch {
            try {
                _isMemberExist.value = Event(Resource.Loading)
                val result = ServerRepository.isIdExist(id, loginType)
                _isMemberExist.value = Event(Resource.Success(result.isExist))
            } catch (domainException: DomainException) {
                _isMemberExist.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isMemberExist.value = Event(Resource.Failure(DomainException(domainErrorSubMessage = "이미 존재하는 회원입니다.", cause = exception)))
            }
        }
    }


    /**
     * SNS 로그인 (모듈)
     *
     * @param context
     * @param type SnsType
     * @param activityResultLauncher 구글 로그인 콜백. (추후 카카오도 사용할 수 있음)
     */
    public fun requestSnsLogin(context: Context, type: LoginType, activityResultLauncher: ActivityResultLauncher<Intent>? = null) {
        _signupUser.value = Event(Resource.Loading)

        viewModelScope.launch {
            when (type) {
                LoginType.KAKAO -> {
                    SnsRepository.kakaoLogin(context) {
                        when (it) {
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                _signupUser.value = Event(Resource.Success(it.data))
                            }
                            is Resource.Failure -> _signupUser.value = Event(Resource.Failure(it.exception))
                        }
                    }
                }

                LoginType.NAVER -> {
                    SnsRepository.naverLogin(context) {
                        when (it) {
                            is Resource.Loading -> {}
                            is Resource.Success -> _signupUser.value = Event(Resource.Success(it.data))
                            is Resource.Failure -> _signupUser.value = Event(Resource.Failure(it.exception))
                        }
                    }
                }

                LoginType.GOOGLE -> {
                    activityResultLauncher?.let {
                        SnsRepository.googleLogin(context, it)
                    }
                }
                else -> {}
            }
        }
    }

    // 휴대폰 인증번호 요청 (회원가입)
    public fun requestPhoneAuth(requestDto: PhoneAuthRequestDto) {
        viewModelScope.launch {
            try {
                _isRequestCert.value = Event(Resource.Loading)
                val result = ServerRepository.phoneAuth(requestDto)
                _isRequestCert.value = Event(Resource.Success(true))
                if (BuildConfig.DEBUG) Log.d(TAG, result.expiredSec.toString())
            } catch (domainException: DomainException) {
                _isRequestCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isRequestCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 휴대폰 인증번호 확인 (회원가입)
    public fun requestPhoneAuthConfirm(mobileNo: String, authValue: String) {
        viewModelScope.launch {
            try {
                _isCompleteCert.value = Event(Resource.Loading)
                val result = ServerRepository.phoneAuthConfirm(mobileNo, authValue)
                _isCompleteCert.value = Event(Resource.Success(true))
                if (BuildConfig.DEBUG) Log.d(TAG, result.message)
            } catch (domainException: DomainException) {
                _isCompleteCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isCompleteCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 비밀번호 변경 인증번호 요청
    public fun requestChangePasswordAuth(mobileNo: String) {
        viewModelScope.launch {
            try {
                _isRequestPwCert.value = Event(Resource.Loading)
                val result = ServerRepository.changePwAuth(mobileNo)
                _isRequestPwCert.value = Event(Resource.Success(true))
            } catch (domainException: DomainException) {
                _isRequestPwCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isRequestPwCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 비밀번호 변경 및 인증번호 확인
    public fun requestChangePassword(mobileNo: String, authValue: String, password: String) {
        viewModelScope.launch {
            try {
                _isCompletePwCert.value = Event(Resource.Loading)
                val result = ServerRepository.changePassword(
                    ChangePasswordRequestDto(
                    mobileNo,
                    authValue,
                    password
                )
                )
                _isCompletePwCert.value = Event(Resource.Success(true))
            } catch (domainException: DomainException) {
                _isCompletePwCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isCompletePwCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 소셜 로그인
    public fun requestLogin() {
        getUser()?.let {
            val snsId = it.socialId!!
            val snsType = LoginType.valueOf(it.socialType!!)
            requestLogin(snsType, snsId, null)
        }
    }

    // 로드801 로그인
    public fun requestLogin(loginType: LoginType, id: String, pw: String?) {
        viewModelScope.launch {
            try {
                _isSuccessLogin.value = Event(Resource.Loading)
                val result = if(loginType == LoginType.DEFAULT) {
                    ServerRepository.loginRoad(id, pw!!)
                } else {
                    ServerRepository.loginSns(loginType, id)
                }

                if (loginType == LoginType.DEFAULT) {
                    // 로그인 정보 저장
                    LocalDatabase.saveAccessToken(
                        loginType = LoginType.DEFAULT,
                        id = id,
                        pw = pw,
                        accessToken = result.accessToken
                    )
                } else {
                    // 로그인 정보 저장
                    LocalDatabase.saveAccessToken(
                        loginType = loginType,
                        id = id,
                        pw = null,
                        accessToken = result.accessToken
                    )
                }
                _isSuccessLogin.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _isSuccessLogin.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isSuccessLogin.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }




    public fun setSignupUser(userDto: UserDto? = null, error: ApiException? = null) {
        viewModelScope.launch {
            _signupUser.value = Event(Resource.Loading)
            if (error == null) {
                userDto?.let {
                    _signupUser.value = Event(Resource.Success(it))
                }
            } else {
                _signupUser.value = Event(Resource.Failure(DomainException("[구글] 로그인 실패","",error)))
            }
        }
    }

    public fun getUser() : UserDto? {
        signupUser.value?.let {
            return (it.peekContent() as Resource.Success<UserDto>).data
        }
        return null
    }

    // 번호 인증 완료
    public fun isCertComplete(): Boolean {
        try {
            isCompleteCert.value?.let {
                return (it.peekContent() as Resource.Success<Boolean>).data
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    public fun resetCert() {
        _isCompleteCert.value = Event(Resource.Success(false))
    }
}