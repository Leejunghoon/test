package com.road801.android.view.main.me

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.log.toMessage
import com.road801.android.BuildConfig
import com.road801.android.common.util.extension.TAG
import com.road801.android.data.network.dto.MeDto
import com.road801.android.data.network.dto.requset.*
import com.road801.android.data.network.dto.response.SuccessResponseDto
import com.road801.android.data.network.dto.response.UploadFileResponseDto
import com.road801.android.data.network.error.DomainException
import com.road801.android.data.repository.ServerRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MeViewModel @Inject constructor() : ViewModel() {

    // 내 정보
    private var _meInfo = MutableLiveData<Event<Resource<MeDto>>>()
    val meInfo: LiveData<Event<Resource<MeDto>>> = _meInfo

    // 휴대폰 인증번호 요청 여부
    private var _isRequestCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isRequestCert: LiveData<Event<Resource<Boolean>>> = _isRequestCert

    // 휴대폰 인증번호 완료 여부
    private var _isCompleteCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isCompleteCert: LiveData<Event<Resource<Boolean>>> = _isCompleteCert

    // 비밀번호 인증번호 요청 여부
    private var _isRequestPwCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isRequestPwCert: LiveData<Event<Resource<Boolean>>> = _isRequestPwCert

    // 비밀번호 인증번호 완료 여부
    private var _isCompletePwCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isCompletePwCert: LiveData<Event<Resource<Boolean>>> = _isCompletePwCert


    // 파일 업로드 완료 여부
    private var _uploadFileInfo = MutableLiveData<Event<Resource<UploadFileResponseDto>>>()
    val uploadFileInfo: LiveData<Event<Resource<UploadFileResponseDto>>> = _uploadFileInfo

    // 프로필 수정 완료 여부
    private var _isCompleteChange = MutableLiveData<Event<Resource<Boolean>>>()
    val isCompleteChange: LiveData<Event<Resource<Boolean>>> = _isCompleteChange

    // 광고 푸쉬 on/off
    private var _isActivePushMarketing = MutableLiveData<Event<Resource<Boolean>>>()
    val isActivePushMarketing: LiveData<Event<Resource<Boolean>>> = _isActivePushMarketing

    // 탈퇴 성공 여부
    private var _isDrop = MutableLiveData<Event<Resource<SuccessResponseDto>>>()
    val isDrop: LiveData<Event<Resource<SuccessResponseDto>>> = _isDrop

    // 탈퇴 요청
    public fun requestWithdrawal(reason: String) {
        viewModelScope.launch {
            try {
                _isDrop.value = Event(Resource.Loading)
                val result = ServerRepository.withdrawal(WithdrawalRequestDto(reason))
                _isDrop.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _isDrop.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isDrop.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 내 정보
    public fun requestMe() {
        viewModelScope.launch {
            try {
                _meInfo.value = Event(Resource.Loading)
                val result = ServerRepository.me()
                _meInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _meInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _meInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 내 정보 수정
    public fun modifyMe(requestDto: MeRequestDto) {
        viewModelScope.launch {
            try {
                _isCompleteChange.value = Event(Resource.Loading)
                val result = ServerRepository.me(requestDto)
                _isCompleteChange.value = Event(Resource.Success(true))
                _meInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _isCompleteChange.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isCompleteChange.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 휴대폰 변경 인증번호 요청
    public fun requestPhoneAuth(mobileNo: String) {
        viewModelScope.launch {
            try {
                _isRequestCert.value = Event(Resource.Loading)
                val result = ServerRepository.phoneAuth(mobileNo)
                _isRequestCert.value = Event(Resource.Success(true))
            } catch (domainException: DomainException) {
                _isRequestCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isRequestCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 휴대폰 변경 및 인증번호 확인
    public fun requestPhoneAuthConfirm(mobileNo: String, authValue: String) {
        viewModelScope.launch {
            try {
                _isCompleteCert.value = Event(Resource.Loading)
                val result = ServerRepository.phoneAuthConfirm(PhoneAuthRequestDto(mobileNo, authValue))
                _isCompleteCert.value = Event(Resource.Success(true))
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
                val result = ServerRepository.changePassword(ChangePasswordRequestDto(
                    mobileNo,
                    authValue,
                    password
                ))
                _isCompletePwCert.value = Event(Resource.Success(true))
            } catch (domainException: DomainException) {
                _isCompletePwCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isCompletePwCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 광고 푸시 on/off
    public fun requestPushAd(isChecked: Boolean) {
        viewModelScope.launch {
            try {
                _isActivePushMarketing.value = Event(Resource.Loading)
                val result = ServerRepository.fcmAd(ActiveRequestDto(isChecked))
                _isActivePushMarketing.value = Event(Resource.Success(isChecked))
            } catch (domainException: DomainException) {
                _isActivePushMarketing.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isActivePushMarketing.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 이미지 업로드
    public fun uploadImageFile(fileUri: Uri) {
        viewModelScope.launch {
            try {
                _uploadFileInfo.value = Event(Resource.Loading)
                val result = ServerRepository.uploadProfileImage(fileUri)
                _uploadFileInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _uploadFileInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _uploadFileInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 푸시 아이디
    public fun updateDeviceID(token: String) {
        viewModelScope.launch {
            try {
                ServerRepository.deviceId(DeviceIdRequestDto("AOS", token))
            } catch (exception: Exception) {
                if (BuildConfig.DEBUG) Log.e(TAG, exception.toMessage())
            }
        }
    }


    // 번호 인증 완료
    public fun isCertComplete(): Boolean {
        isCompleteCert.value?.let {
            return (it.peekContent() as Resource.Success<Boolean>).data
        }
        return false
    }

    public fun getMe(): MeDto? {
        meInfo.value?.let {
            return (it.peekContent() as Resource.Success<MeDto>).data
        }
        return null
    }

    public fun resetCert() {
        _isCompleteCert.value = Event(Resource.Success(false))
    }
}