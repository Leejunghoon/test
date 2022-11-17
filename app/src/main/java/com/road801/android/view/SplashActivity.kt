package com.road801.android.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.road801.android.R
import com.road801.android.common.util.extension.goToHome
import com.road801.android.common.util.extension.goToIntro
import com.road801.android.data.repository.LocalRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val activityScope = CoroutineScope(Dispatchers.Main)
        activityScope.launch {
            delay(1000)
            Log.d("hoon LocalRepository.isLogin", "${LocalRepository.isLogin}")
            if (LocalRepository.isLogin) {
                goToHome()
            } else {
                goToIntro()
            }
        }
    }
}