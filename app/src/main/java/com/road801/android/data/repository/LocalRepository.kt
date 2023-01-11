package com.road801.android.data.repository

import com.road801.android.data.network.interceptor.LocalDatabase


object LocalRepository {

    /**
     * 로그인 상태 유무 확인
     */
    val isLogin = !LocalDatabase.fetchAccessToken().isNullOrEmpty()

    fun preload() {
        val preload = isLogin
    }

}