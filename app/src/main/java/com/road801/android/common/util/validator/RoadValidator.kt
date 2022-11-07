package com.road801.android.common.util.validator

class RoadValidator {
    companion object {
        fun validPassword(reg: String): Boolean {
//            val regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\]\\[/~!@#$%^&*()':;,.+=<>{}?|_\"-])[A-Za-z\\d\\]\\[/~!@#$%^&*()':;,.+=<>{}?|_\"-]{8,}\$"
            val regex = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,20}$" // 영문, 숫자
            return reg.matches(regex.toRegex())
        }

        fun validNickName(reg: String): Boolean {
            val regex = "^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]{2,8}$"
            return reg.matches(regex.toRegex())
        }

        fun validId(reg: String): Boolean {
            val regex = "^[a-z|A-Z|0-9|]{8,20}$"
            return reg.matches(regex.toRegex())
        }

        fun validAuthNum(reg: String): Boolean {
            val regex = "^[0-9]{6}$" // 인증번호 6자리
            return reg.matches(regex.toRegex())
        }
    }
}

fun email(email: String): Boolean = email.matches(android.util.Patterns.EMAIL_ADDRESS.toRegex())
fun phone(phone: String): Boolean = phone.matches(android.util.Patterns.PHONE.toRegex())
fun phone(phone: Int): Boolean = phone(phone.toString())
fun password(password: String): Boolean = RoadValidator.validPassword(password)
fun authNum(number: String): Boolean = RoadValidator.validAuthNum(number)

fun String.validNickName(): Boolean = RoadValidator.validNickName(this)
fun String.validId(): Boolean = RoadValidator.validId(this)
fun String.validPassword(): Boolean = RoadValidator.validPassword(this)
fun String.validAuthNum(): Boolean = RoadValidator.validAuthNum(this)