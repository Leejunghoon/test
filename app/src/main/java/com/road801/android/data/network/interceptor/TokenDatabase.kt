package com.road801.android.data.network.interceptor;

import android.content.SharedPreferences
import com.road801.android.common.enum.LoginType

object TokenDatabase {

    public lateinit var sharedPreferences: SharedPreferences
    private const val PREFERENCE_ACCESS_TOKEN_KEY = "PREFERENCE_ACCESS_TOKEN_KEY"
    private const val PREFERENCE_KEY_LOGIN_TYPE = "PREFERENCE_KEY_LOGIN_TYPE"
    private const val PREFERENCE_KEY_ID = "PREFERENCE_KEY_ID"
    private const val PREFERENCE_KEY_PW = "PREFERENCE_KEY_PW"

    fun fetchAccessToken(): String? = sharedPreferences.getString(PREFERENCE_ACCESS_TOKEN_KEY, null)
    fun fetchLoginType(): String? = sharedPreferences.getString(PREFERENCE_KEY_LOGIN_TYPE, null)
    fun fetchLoginId(): String? = sharedPreferences.getString(PREFERENCE_KEY_ID, null)
    fun fetchLoginPw(): String? = sharedPreferences.getString(PREFERENCE_KEY_PW, null)


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

    fun deleteAccessToken() {
        sharedPreferences.edit().apply {
            putString(PREFERENCE_ACCESS_TOKEN_KEY, null)
            apply()
        }
    }
}