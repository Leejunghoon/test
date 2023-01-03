package com.road801.android.view.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.response.HomeEventResponseDto
import com.road801.android.data.network.dto.response.HomeResponseDto
import com.road801.android.data.network.error.DomainException
import com.road801.android.data.network.interceptor.LocalDatabase
import com.road801.android.data.repository.ServerRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // 홈 정보
    private var _homeInfo = MutableLiveData<Event<Resource<HomeResponseDto>>>()
    val homeInfo: LiveData<Event<Resource<HomeResponseDto>>> = _homeInfo

    // 홈 이벤트 정보
    private var _homeEventInfo = MutableLiveData<Event<Resource<HomeEventResponseDto>>>()
    val homeEventInfo: LiveData<Event<Resource<HomeEventResponseDto>>> = _homeEventInfo

    private var _isNew = MutableLiveData<Event<Resource<Boolean>>>()
    val isNew: LiveData<Event<Resource<Boolean>>> = _isNew

    // 홈 정보 조회
    public fun requestHomeInfo() {
        viewModelScope.launch {
            try {
                _homeInfo.value = Event(Resource.Loading)
                val result = ServerRepository.home()
                _homeInfo.value = Event(Resource.Success(result))
                LocalDatabase.saveAlertCount(result.alertCount)
            } catch (domainException: DomainException) {
                _homeInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _homeInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 홈 이벤트 조회
    public fun requestHomeEventInfo() {
        viewModelScope.launch {
            try {
                _homeEventInfo.value = Event(Resource.Loading)
                val result = ServerRepository.homeEvent()
                _homeEventInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _homeEventInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _homeEventInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    public fun findNewAlert() {
        viewModelScope.launch {
            try {
                _isNew.value = Event(Resource.Loading)
                _isNew.value = Event(Resource.Success(checkNewAlert()))

            } catch (domainException: DomainException) {
                _isNew.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _isNew.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }


    //새로운 알림이 있는지 여부
    private fun checkNewAlert() = LocalDatabase.fetchAlertCount() > 0


}
