package com.road801.android.view.main.me

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.MeDto
import com.road801.android.data.network.dto.requset.WithdrawalRequestDto
import com.road801.android.data.network.dto.response.SuccessResponseDto
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


    public fun getMe(): MeDto? {
        meInfo.value?.let {
            return (it.peekContent() as Resource.Success<MeDto>).data
        }
        return null
    }
}