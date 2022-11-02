package com.road801.android.common

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.road801.android.BuildConfig
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class GlobalApplication : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        // KaKao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        val keyHash = Utility.getKeyHash(this) // for debug
        if (BuildConfig.DEBUG) {
            Log.d("GlobalApplication", "keyHash: $keyHash")
        }
        Log.d("GlobalApplication", "keyHash: $keyHash")
    }

    companion object {
        lateinit var instance: GlobalApplication
    }
}