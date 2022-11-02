package com.sixdotfive.budkup.data.network.interceptor.token

import android.content.Context
import android.content.SharedPreferences
import com.road801.android.common.GlobalApplication

object TokenDatabase {

    private var sharedPreferences: SharedPreferences = GlobalApplication.instance.getSharedPreferences("token_database", Context.MODE_PRIVATE)
    private const val PREFERENCE_KEY = "PREFERENCE_KEY_TOKEN"
    private const val PREFERENCE_KEY_SIGNUP_TYPE = "PREFERENCE_KEY_TYPE"
    private const val PREFERENCE_KEY_BUDKUP_ID = "PREFERENCE_KEY_BUDKUP_ID"

//    fun saveAccessToken(signUpIdType: SignUpIdType, budKupId: String, accessToken: String) {
//        sharedPreferences.edit().apply {
//            putString(PREFERENCE_KEY, accessToken)
//            putString(PREFERENCE_KEY_SIGNUP_TYPE, signUpIdType.name)
//            putString(PREFERENCE_KEY_BUDKUP_ID, budKupId)
//            apply()
//        }
//    }

    fun fetchAccessToken(): String? = sharedPreferences.getString(PREFERENCE_KEY, null)
    fun fetchSignUpType(): String? = sharedPreferences.getString(PREFERENCE_KEY_SIGNUP_TYPE, null) // TODO: 토큰과 통합 클래스로 추출
    fun fetchBudKupId(): String? = sharedPreferences.getString(PREFERENCE_KEY_BUDKUP_ID, null) // TODO: 토큰과 통합 클래스로 추출


    fun deleteAccessToken() {
        sharedPreferences.edit().apply {
            putString(PREFERENCE_KEY, null)
            apply()
        }
    }
}