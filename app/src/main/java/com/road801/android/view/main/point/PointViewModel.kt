package com.road801.android.view.main.point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.PaginationDto
import com.road801.android.data.network.dto.PointHistoryDto
import com.road801.android.data.network.dto.response.CommonListResponseDto
import com.road801.android.data.network.error.DomainException
import com.road801.android.data.repository.ServerRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PointViewModel @Inject constructor() : ViewModel() {

    // 포인트 내역 정보
    private var _pointHistoryInfo = MutableLiveData<Event<Resource<CommonListResponseDto<PointHistoryDto>>>>()
    val pointHistoryInfo: LiveData<Event<Resource<CommonListResponseDto<PointHistoryDto>>>> = _pointHistoryInfo

    public val pagination = PaginationDto(sort = emptyList())
    private var previousData = mutableListOf<PointHistoryDto>()

    // 포인트 내역 조회
    public fun requestPointHistory() {
        viewModelScope.launch {
            try {
                _pointHistoryInfo.value = Event(Resource.Loading)
                val result = ServerRepository.pointHistory(pagination)
                pagination.nextId = result.meta.nextId
                previousData.addAll(result.data.toMutableList()) // set first data
                _pointHistoryInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _pointHistoryInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _pointHistoryInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 포인트 내역 조회
    public fun requestMorePointHistory() {
        pagination.nextId?.let {
            viewModelScope.launch {
                try {
                    _pointHistoryInfo.value = Event(Resource.Loading)
                    val result = ServerRepository.pointHistory(pagination)

                    previousData.addAll(result.data.toMutableList())
                    result.data = previousData.toList()
                    pagination.nextId = result.meta.nextId

                    _pointHistoryInfo.value = Event(Resource.Success(result))
                } catch (domainException: DomainException) {
                    _pointHistoryInfo.value = Event(Resource.Failure(domainException))
                } catch (exception: Exception) {
                    _pointHistoryInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
                }
            }
        }
    }

    public fun onDestroyView() {
        pagination.nextId = null
        previousData.clear()
    }

}