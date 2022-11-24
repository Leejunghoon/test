package com.road801.android.view.main.home.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.EventDto
import com.road801.android.data.network.dto.PaginationDto
import com.road801.android.data.network.dto.PointDto
import com.road801.android.data.network.dto.response.CommonListResponseDto
import com.road801.android.data.network.error.DomainException
import com.road801.android.data.repository.ServerRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EventViewModel @Inject constructor() : ViewModel() {
    private val pagination = PaginationDto(page = 0, size = 20, sort = emptyList())

    // 이벤트 정보
    private var _eventInfo = MutableLiveData<Event<Resource<CommonListResponseDto<EventDto>>>>()
    val eventInfo: LiveData<Event<Resource<CommonListResponseDto<EventDto>>>> = _eventInfo

    // 이벤트 상세 정보
    private var _eventDetail = MutableLiveData<Event<Resource<EventDto>>>()
    val eventDetail: LiveData<Event<Resource<EventDto>>> = _eventDetail

    // 이벤트 참여 (현재 남은 포인트)
    private var _eventCurrentPoint = MutableLiveData<Event<Resource<PointDto>>>()
    val eventCurrentPoint: LiveData<Event<Resource<PointDto>>> = _eventCurrentPoint



    // 이벤트 리스트 조회
    public fun requestEvent() {
        viewModelScope.launch {
            try {
                _eventInfo.value = Event(Resource.Loading)
                val result = ServerRepository.event(pagination)
                _eventInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _eventInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _eventInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 이벤트 상세 조회
    public fun requestEventDetail(eventId: Int) {
        viewModelScope.launch {
            try {
                _eventDetail.value = Event(Resource.Loading)
                val result = ServerRepository.eventDetail(eventId)
                _eventDetail.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _eventDetail.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _eventDetail.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 이벤트 참여
    public fun requestEventEnter(eventId: Int) {
        viewModelScope.launch {
            try {
                _eventCurrentPoint.value = Event(Resource.Loading)
                val result = ServerRepository.eventEnter(eventId)
                _eventCurrentPoint.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _eventCurrentPoint.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _eventCurrentPoint.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

}
