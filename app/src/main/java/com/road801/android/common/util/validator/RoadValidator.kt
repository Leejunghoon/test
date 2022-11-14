package com.road801.android.common.util.validator

class RoadValidator {
    companion object {
        fun password(reg: String): Boolean {
//            val regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\]\\[/~!@#$%^&*()':;,.+=<>{}?|_\"-])[A-Za-z\\d\\]\\[/~!@#$%^&*()':;,.+=<>{}?|_\"-]{8,}\$"
//            val regex = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{10,}$" // 영문, 숫자만
            val regex = "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{10,}\$"
            return reg.matches(regex.toRegex())
        }

        fun nickName(reg: String): Boolean {
            val regex = "^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]{2,8}$"
            return reg.matches(regex.toRegex())
        }

        fun name(reg: String): Boolean {
            val regex = "^[ㄱ-ㅎ|가-힣|a-z|A-Z]{2,}$"
            return reg.matches(regex.toRegex())
        }

        fun id(reg: String): Boolean {
            val regex = "^[a-z|A-Z|0-9|]{8,20}$"
            return reg.matches(regex.toRegex())
        }

        fun certNum(reg: String): Boolean {
            val regex = "^[0-9]{6}$" // 인증번호 6자리
            return reg.matches(regex.toRegex())
        }

        fun phone(reg: String): Boolean {
            return reg.matches(android.util.Patterns.PHONE.toRegex())
        }

        fun email(reg: String): Boolean {
            return reg.matches(android.util.Patterns.EMAIL_ADDRESS.toRegex())
        }
    }
}

