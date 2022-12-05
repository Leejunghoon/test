package com.road801.android.view

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val activityScope = CoroutineScope(Dispatchers.Main)
        activityScope.launch {
            delay(1500)
            if (!checkPermission(CALL_PHONE) || !checkPermission(READ_EXTERNAL_STORAGE)) {
                requestPermission()
            } else {
                moveNextScreen()
            }
        }
    }

    private fun moveNextScreen() {
        if (LocalRepository.isLogin) {
            goToHome()
        } else {
            goToIntro()
        }
    }


    private fun checkPermission(permission: String): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(this, permission)
        return hasPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(CALL_PHONE, READ_EXTERNAL_STORAGE, POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(CALL_PHONE, READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                moveNextScreen()
                return
            }
            else -> Unit
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}