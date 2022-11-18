package com.road801.android.view.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.response.HomeEventResponseDto
import com.road801.android.data.network.dto.response.HomeResponseDto
import com.road801.android.data.network.error.DomainException
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

    private var _userGrade = MutableLiveData<String>("")
    public val userGrade: LiveData<String> = _userGrade

    private var _userPoint = MutableLiveData<Int>(0)
    public val userPoint: LiveData<Int> = _userPoint


    // 홈 정보 조회
    public fun requestHomeInfo() {
        viewModelScope.launch {
            try {
                _homeInfo.value = Event(Resource.Loading)
                val result = ServerRepository.home()
                _homeInfo.value = Event(Resource.Success(result))

                _userGrade = MutableLiveData(result.customerInfo.rating.value)
                _userPoint = MutableLiveData(result.pointInfo.point)

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
}
