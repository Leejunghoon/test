package com.road801.android.data.network.interceptor;

import android.content.SharedPreferences
import com.road801.android.common.enum.LoginType

object LocalDatabase {

    public lateinit var sharedPreferences: SharedPreferences
    private const val PREFERENCE_ACCESS_TOKEN_KEY = "PREFERENCE_ACCESS_TOKEN_KEY"
    private const val PREFERENCE_KEY_LOGIN_TYPE = "PREFERENCE_KEY_LOGIN_TYPE"
    private const val PREFERENCE_KEY_ID = "PREFERENCE_KEY_ID"
    private const val PREFERENCE_KEY_PW = "PREFERENCE_KEY_PW"
    private const val PREFERENCE_ALERT_COUNT = "PREFERENCE_ALERT_COUNT"
    private const val PREFERENCE_PREVIOUS_ALERT_COUNT = "PREFERENCE_PREVIOUS_ALERT_COUNT"

    fun fetchAccessToken(): String? = sharedPreferences.getString(PREFERENCE_ACCESS_TOKEN_KEY, null)
    fun fetchLoginType(): String? = sharedPreferences.getString(PREFERENCE_KEY_LOGIN_TYPE, null)
    fun fetchLoginId(): String? = sharedPreferences.getString(PREFERENCE_KEY_ID, null)
    fun fetchLoginPw(): String? = sharedPreferences.getString(PREFERENCE_KEY_PW, null)
    fun fetchAlertCount(): Int = sharedPreferences.getInt(PREFERENCE_ALERT_COUNT, 0)
    fun fetchPreviousAlertCount(): Int = sharedPreferences.getInt(PREFERENCE_PREVIOUS_ALERT_COUNT, -1)


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
            clear()
            apply()
        }
    }

    fun savePreviousAlertCount(size: Int) {
        sharedPreferences.edit().apply {
            putInt(PREFERENCE_PREVIOUS_ALERT_COUNT, size)
            apply()
        }
    }

    fun saveAlertCount(size: Int) {
        sharedPreferences.edit().apply {
            putInt(PREFERENCE_ALERT_COUNT, size)
            apply()
        }
    }

    fun saveId(id: String) {
        sharedPreferences.edit().apply {
            putString(PREFERENCE_KEY_ID, id)
        }.apply()
    }
}