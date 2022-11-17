package com.road801.android.common

import android.app.Application
import android.content.Context
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.navercorp.nid.NaverIdLoginSDK
import com.road801.android.BuildConfig
import com.road801.android.R
import com.road801.android.data.network.interceptor.TokenDatabase
import com.road801.android.data.repository.SnsRepository
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class GlobalApplication : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        initSharedPreference()
        setupKaKaoSDK()
        setupNaverSDK()
    }

    companion object {
        lateinit var instance: GlobalApplication
    }


    private fun initSharedPreference() {
        TokenDatabase.sharedPreferences = getSharedPreferences("road801_db", Context.MODE_PRIVATE)
    }

    private fun setupKaKaoSDK() {
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    private fun setupNaverSDK() {
        val naverClientId = getString(R.string.naver_client_id)
        val naverClientSecret = getString(R.string.naver_client_secret)
        val naverClientName = getString(R.string.naver_client_name)
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)
    }
}