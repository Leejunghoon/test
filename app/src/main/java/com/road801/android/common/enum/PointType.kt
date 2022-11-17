package com.road801.android.common.enum

/**
 *  EARN 적립, USE 사용
 *
 */
enum class PointType  {
    USE, USE_CANCEL, EARN, EARN_CANCEL;

    var koValue: String
        get() = when(this) {
            USE -> "사용"
            USE_CANCEL -> "사용취소"
            EARN -> "충전"
            EARN_CANCEL -> "충전취소"
        }
        set(value) = TODO()
}