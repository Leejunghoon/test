package com.road801.android.view.main.home.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.AlertDto
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.data.network.dto.PaginationDto
import com.road801.android.data.network.dto.response.CommonListResponseDto
import com.road801.android.data.network.error.DomainException
import com.road801.android.data.network.interceptor.LocalDatabase
import com.road801.android.data.repository.ServerRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlertViewModel @Inject constructor() : ViewModel() {
    // 알림 정보
    private var _alertInfo = MutableLiveData<Event<Resource<CommonListResponseDto<AlertDto>>>>()
    val alertInfo: LiveData<Event<Resource<CommonListResponseDto<AlertDto>>>> = _alertInfo

    public val pagination = PaginationDto(sort = emptyList())
    private var previousData = mutableListOf<AlertDto>()

    // 알림 조회
    public fun requestAlertInfo() {
        viewModelScope.launch {
            try {
                _alertInfo.value = Event(Resource.Loading)
                val result = ServerRepository.alert(pagination)
                pagination.nextId = result.meta.nextId
                previousData.addAll(result.data.toMutableList()) // set first data
                _alertInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _alertInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _alertInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 알림 조회
    public fun requestMoreAlertInfo() {
        pagination.nextId?.let {
            viewModelScope.launch {
                try {
                    _alertInfo.value = Event(Resource.Loading)
                    val result = ServerRepository.alert(pagination)

                    previousData.addAll(result.data.toMutableList())
                    result.data = previousData.toList()
                    pagination.nextId = result.meta.nextId

                    _alertInfo.value = Event(Resource.Success(result))
                } catch (domainException: DomainException) {
                    _alertInfo.value = Event(Resource.Failure(domainException))
                } catch (exception: Exception) {
                    _alertInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
                }
            }
        }

    }


    public fun onDestroyView() {
        pagination.nextId = null
        previousData.clear()
    }
}
