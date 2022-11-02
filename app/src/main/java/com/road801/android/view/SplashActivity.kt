package com.road801.android.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.road801.android.R
import com.road801.android.common.goToHomeActivity
import com.road801.android.common.goToIntroActivity
import com.road801.android.data.repository.LocalRepository
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_splash)


        if (LocalRepository.isLogin) {
            goToHomeActivity()
        } else {
            goToIntroActivity()
        }
    }
}