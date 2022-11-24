package com.road801.android.view.main.home.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.data.network.dto.NewsDetailDto
import com.road801.android.data.network.dto.NewsDto
import com.road801.android.data.network.dto.PaginationDto
import com.road801.android.data.network.dto.UserDto
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
class NewsViewModel @Inject constructor() : ViewModel() {
    private val pagination = PaginationDto(page = 0, size = 20, sort = emptyList())
    public var tempNewsItems = mutableListOf<NewsDto>()

    public var isMore = false

    // 소식 정보
    private var _newsInfo = MutableLiveData<Event<Resource<CommonListResponseDto<NewsDto>>>>()
    val newsInfo: LiveData<Event<Resource<CommonListResponseDto<NewsDto>>>> = _newsInfo

    // 소식 상세 정보
    private var _newsDetail = MutableLiveData<Event<Resource<NewsDetailDto>>>()
    val newsDetail: LiveData<Event<Resource<NewsDetailDto>>> = _newsDetail

    // 소식 조회
    public fun requestNewsInfo() {
        viewModelScope.launch {
            try {
                _newsInfo.value = Event(Resource.Loading)
                val result = ServerRepository.news(pagination)

//                // paging
//                if (result.data.isNotEmpty()) {
//                    with(result.data.size % pagination.size == 0) {
//                        isMore = this
//                        if (this) pagination.page += 1
//                    }
//                } else {
//                    isMore = false
//                }
//
//                tempNewsItems.addAll(result.data)
//                result.data = tempNewsItems

                _newsInfo.value = Event(Resource.Success(result))
                LocalDatabase.saveNewsSize(result.total)
            } catch (domainException: DomainException) {
                _newsInfo.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _newsInfo.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    // 소식 상세 조회
    public fun requestNewsDetail(boardId: Int) {
        viewModelScope.launch {
            try {
                _newsDetail.value = Event(Resource.Loading)
                val result = ServerRepository.newsDetail(boardId)
                _newsDetail.value = Event(Resource.Success(result))
            } catch (domainException: DomainException) {
                _newsDetail.value = Event(Resource.Failure(domainException))
            } catch (exception: Exception) {
                _newsDetail.value = Event(Resource.Failure(DomainException(cause = exception)))
            }
        }
    }

    private fun getNewData(): CommonListResponseDto<NewsDto>? {
        newsInfo.value?.let {
            return (it.peekContent() as Resource.Success<CommonListResponseDto<NewsDto>>).data
        }
        return null
    }
}
