package com.road801.android.view.intro

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.road801.android.common.enum.SnsType
import com.road801.android.data.network.dto.UserDto
import com.road801.android.data.repository.SnsRepository
import com.road801.android.domain.transfer.Event
import com.road801.android.domain.transfer.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor() : ViewModel() {

    /**
     * 회원가입 프로세스 데이터 (공통)
     */
    private var _signupUser = MutableLiveData<Event<Resource<UserDto>>>()
    val signupUser: LiveData<Event<Resource<UserDto>>> = _signupUser

    /**
     * 이미 가입한 회원 존재 유무
     */
    private var _memberExist = MutableLiveData<Event<Resource<Boolean>>>()
    val memberExist: LiveData<Event<Resource<Boolean>>> = _memberExist



    public fun requestSnsLogin(context: Context, type: SnsType) {
        _signupUser.value = Event(Resource.Loading)

        viewModelScope.launch {
            when (type) {
                SnsType.KAKAO -> {
                    SnsRepository.kakaoLogin(context) {
                        when (it) {
                            is Resource.Loading -> {}
                            is Resource.Success -> _signupUser.value = Event(Resource.Success(it.data))
                            is Resource.Failure -> _signupUser.value = Event(Resource.Failure(it.exception))
                        }
                    }
                }
                SnsType.NAVER -> {

                }
                SnsType.GOOGLE -> {

                }
            }
        }

    }
}