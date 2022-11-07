package com.road801.android.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.road801.android.R
import com.road801.android.common.util.extension.goToHome
import com.road801.android.common.util.extension.goToIntro
import com.road801.android.data.repository.LocalRepository
import dagger.hilt.android.AndroidEntryPoint

//@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (LocalRepository.isLogin) {
            goToHome()
        } else {
            goToIntro()
        }
    }
}