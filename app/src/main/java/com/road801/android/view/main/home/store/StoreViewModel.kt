package com.road801.android.view.main.home.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.*
import com.road801.android.data.network.dto.response.CommonListResponseDto
import com.road801.android.data.network.error.DomainException
import com.road801.android.data.repository.ServerRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor() : ViewModel() {
//    private val pagination = PaginationDto(sort = emptyList())

    // 매장 정보
    private var _storeInfo = MutableLiveData<Event<Resource<CommonListResponseDto<StoreDto>>>>()
    val storeInfo: LiveData<Event<Resource<CommonListResponseDto<StoreDto>>>> = _storeInfo

    // 매장 상세 정보
    private var _storeDetail = MutableLiveData<Event<Resource<StoreDetailDto>>>()
    val storeDetail: LiveData<Event<Resource<StoreDetailDto>>> = _storeDetail



    // 매장 조회
    public fun requestStoreInfo() {
        viewModelScope.launch {
            try {
                _storeInfo.value = Event(Resource.Loading)
                val result = ServerRepository.store()
                _storeInfo.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _storeInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _storeInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 매장 상세 조회
    public fun requestStoreDetail(storeId: Int) {
        viewModelScope.launch {
            try {
                _storeDetail.value = Event(Resource.Loading)
                val result = ServerRepository.storeDetail(storeId)
                _storeDetail.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _storeDetail.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _storeDetail.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 매장 전화번호
    public fun getTel() = (storeDetail.value?.peekContent() as Resource.Success<StoreDetailDto>).data.phone
}
