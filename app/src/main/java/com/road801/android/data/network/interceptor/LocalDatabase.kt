package com.road801.android.data.network.interceptor;

import android.content.SharedPreferences
import com.road801.android.common.enum.LoginType

object LocalDatabase {

    public lateinit var sharedPreferences: SharedPreferences
    private const val PREFERENCE_ACCESS_TOKEN_KEY = "PREFERENCE_ACCESS_TOKEN_KEY"
    private const val PREFERENCE_KEY_LOGIN_TYPE = "PREFERENCE_KEY_LOGIN_TYPE"
    private const val PREFERENCE_KEY_ID = "PREFERENCE_KEY_ID"
    private const val PREFERENCE_KEY_PW = "PREFERENCE_KEY_PW"
    private const val PREFERENCE_NEWS_SIZE = "PREFERENCE_NEWS_SIZE"
    private const val PREFERENCE_PREVIOUS_NEWS_SIZE = "PREFERENCE_PREVIOUS_NEWS_SIZE"

    fun fetchAccessToken(): String? = sharedPreferences.getString(PREFERENCE_ACCESS_TOKEN_KEY, null)
    fun fetchLoginType(): String? = sharedPreferences.getString(PREFERENCE_KEY_LOGIN_TYPE, null)
    fun fetchLoginId(): String? = sharedPreferences.getString(PREFERENCE_KEY_ID, null)
    fun fetchLoginPw(): String? = sharedPreferences.getString(PREFERENCE_KEY_PW, null)
    fun fetchNewsSize(): Int = sharedPreferences.getInt(PREFERENCE_NEWS_SIZE, 0)
    fun fetchPreviousNewsSize(): Int = sharedPreferences.getInt(PREFERENCE_NEWS_SIZE, -1)


    fun saveAccessToken(loginType: LoginType, id: String, pw: String?, accessToken: String) {
        sharedPreferences.edit().apply {
            putString(PREFERENCE_ACCESS_TOKEN_KEY, accessToken)
            putString(PREFERENCE_KEY_LOGIN_TYPE, loginType.name)
            putString(PREFERENCE_KEY_ID, id)
            pw?.let {
                putString(PREFERENCE_KEY_PW, it)
            }
            apply()
        }
    }

    fun logOut() {
        sharedPreferences.edit().apply {
            remove(PREFERENCE_ACCESS_TOKEN_KEY)
            remove(PREFERENCE_KEY_LOGIN_TYPE)
            remove(PREFERENCE_KEY_ID)
            remove(PREFERENCE_KEY_PW)
            apply()
        }
    }

    fun savePreviousNewsSize(size: Int) {
        sharedPreferences.edit().apply {
            putInt(PREFERENCE_PREVIOUS_NEWS_SIZE, size)
            apply()
        }
    }

    fun saveNewsSize(size: Int) {
        sharedPreferences.edit().apply {
            putInt(PREFERENCE_NEWS_SIZE, size)
            apply()
        }
    }
}