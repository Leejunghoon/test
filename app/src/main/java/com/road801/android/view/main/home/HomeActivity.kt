package com.road801.android.view.main.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.road801.android.R
import com.road801.android.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val MENU_HOME = R.id.bottom_menu_home       // 홈
    private val MENU_POINT = R.id.bottom_menu_point     // 포인트
    private val MENU_MY_INFO = R.id.bottom_menu_my_info // 내 정보

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_home)

        binding.homeBottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                MENU_HOME -> {
                    Toast.makeText(this, "bottom_menu_home",Toast.LENGTH_SHORT).show()
                    return@setOnItemSelectedListener true
                }
                MENU_POINT -> {
                    Toast.makeText(this, "bottom_menu_point",Toast.LENGTH_SHORT).show()
                    return@setOnItemSelectedListener true
                }
                MENU_MY_INFO -> {
                    Toast.makeText(this, "bottom_menu_my_info",Toast.LENGTH_SHORT).show()
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }

    }
}