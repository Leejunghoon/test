package com.road801.android.data.repository

import com.road801.android.data.network.interceptor.TokenDatabase


object LocalRepository {

    /**
     * 로그인 상태 유무 확인
     */
    val isLogin = !TokenDatabase.fetchAccessToken().isNullOrEmpty()


}