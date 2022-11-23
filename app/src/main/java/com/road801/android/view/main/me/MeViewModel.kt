package com.road801.android.view.main.me

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.BuildConfig
import com.road801.android.common.util.extension.TAG
import com.road801.android.data.network.dto.MeDto
import com.road801.android.data.network.dto.requset.MeRequestDto
import com.road801.android.data.network.dto.requset.PhoneAuthRequestDto
import com.road801.android.data.network.dto.requset.WithdrawalRequestDto
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

    // 휴대폰 인증 요청 여부
    private var _isRequestCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isRequestCert: LiveData<Event<Resource<Boolean>>> = _isRequestCert

    // 휴대폰 인증 완료 여부
    private var _isCompleteCert = MutableLiveData<Event<Resource<Boolean>>>()
    val isCompleteCert: LiveData<Event<Resource<Boolean>>> = _isCompleteCert

    // 파일 업로드 완료 여부
    private var _uploadFileInfo = MutableLiveData<Event<Resource<UploadFileResponseDto>>>()
    val uploadFileInfo: LiveData<Event<Resource<UploadFileResponseDto>>> = _uploadFileInfo

    // 프로필 수정 완료 여부
    private var _isCompleteChange = MutableLiveData<Event<Resource<Boolean>>>()
    val isCompleteChange: LiveData<Event<Resource<Boolean>>> = _isCompleteChange

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

    // 휴대폰 인증 요청
    public fun requestPhoneAuth(mobileNo: String) {
        viewModelScope.launch {
            try {
                _isRequestCert.value = Event(Resource.Loading)
                val result = ServerRepository.phoneAuth(mobileNo)
                _isRequestCert.value = Event(Resource.Success(true))
                if (BuildConfig.DEBUG) Log.d(TAG, result.expiredSec.toString())
            } catch (domainException: DomainException) {
                _isRequestCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isRequestCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 휴대폰 인증 완료
    public fun requestPhoneAuthConfirm(requestDto: PhoneAuthRequestDto) {
        viewModelScope.launch {
            try {
                _isCompleteCert.value = Event(Resource.Loading)
                val result = ServerRepository.phoneAuthConfirm(requestDto)
                _isCompleteCert.value = Event(Resource.Success(true))
                if (BuildConfig.DEBUG) Log.d(TAG, result.message)
            } catch (domainException: DomainException) {
                _isCompleteCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isCompleteCert.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 비밀번호 변경
    public fun requestChangePassword(requestDto: PhoneAuthRequestDto) {
        viewModelScope.launch {
            try {
                _isCompleteCert.value = Event(Resource.Loading)
                val result = ServerRepository.phoneAuthConfirm(requestDto)
                _isCompleteCert.value = Event(Resource.Success(true))
                if (BuildConfig.DEBUG) Log.d(TAG, result.message)
            } catch (domainException: DomainException) {
                _isCompleteCert.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isCompleteCert.value = Event(Resource.Failure(DomainException(cause = exception)))
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