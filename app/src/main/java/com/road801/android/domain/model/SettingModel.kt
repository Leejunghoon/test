package com.road801.android.domain.model

enum class SettingType {
    PUSH, TERMS, LOGOUT, WITHDRAWAL;

    var koString: String
        get() =  when(this) {
            PUSH -> "푸쉬알림 설정"
            TERMS -> "이용약관"
            LOGOUT -> "로그아웃"
            WITHDRAWAL -> "서비스 해지 및 철회"
        }
        private set(value) {}

}

data class SettingModel(
    val type: SettingType,
    val image: Int,
    val title: String
)