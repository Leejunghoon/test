package com.road801.android.common.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.road801.android.BuildConfig
import com.road801.android.R
import com.road801.android.common.enum.NotificationType
import com.road801.android.common.util.extension.TAG
import com.road801.android.view.main.home.HomeActivity


class RoadFirebaseMessagingService : FirebaseMessagingService() {
    private val CHANNEL_NAME = "Push Notification"
    private val CHANNEL_DESCRIPTION = "FCM"
    private val CHANNEL_ID = "ROAD801"

    companion object {
        public fun geToken(callback: (String)-> Unit) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                if(BuildConfig.DEBUG) Log.  d(TAG, token)
                callback.invoke(token)
            })
        }
    }

    // 앱 설치 후 첫 실행시 토큰값 또는 갱신시 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if(BuildConfig.DEBUG) Log.d(TAG, "Refreshed token: $token")
    }

    /* 메세지 수신 메서드 */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if(BuildConfig.DEBUG) Log.d(TAG, "onMessageReceived() - from : ${remoteMessage.from}")

        val type = remoteMessage.data["type"]?.let { NotificationType.valueOf(it) } ?: kotlin.run {
            NotificationType.NORMAL  //type 이 null 이면 NORMAL type 으로 처리
        }

        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body

        if(BuildConfig.DEBUG) Log.d(TAG, "onMessageReceived() - type : $type")
        if(BuildConfig.DEBUG) Log.d(TAG, "onMessageReceived() - title : $title")
        if(BuildConfig.DEBUG) Log.d(TAG, "onMessageReceived() - message : $message")

        sendNotification(type, title, message)
    }


    private fun sendNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Oreo(26) 이상 버전에는 channel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(channel)
        }

        //알림 생성
        NotificationManagerCompat.from(this)
            .notify((System.currentTimeMillis()/100).toInt(), createNotification(type, title, message))  //알림이 여러개 표시되도록 requestCode 를 추가
    }


    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {

        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("notificationType", " ${type.title} 타입 ")
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(this, (System.currentTimeMillis()/100).toInt(), intent, PendingIntent.FLAG_IMMUTABLE)  //알림이 여러개 표시되도록 requestCode 를 추가

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_small_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  //알림 눌렀을 때 실행할 Intent 설정
            .setAutoCancel(true)  //클릭 시 자동으로 삭제되도록 설정

        //type 에 따라 style 설정
        when (type) {
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$message")
                )
            }
            NotificationType.CUSTOM -> Unit
        //            {
//                notificationBuilder.setStyle(
//                    NotificationCompat.DecoratedCustomViewStyle()
//                )
//                    .setCustomContentView(
//                        RemoteViews(
//                            packageName,
//                            R.layout.view_custom_notification
//                        ).apply {
//                            setTextViewText(R.id.tv_custom_title, title)
//                            setTextViewText(R.id.tv_custom_message, message)
//                        }
//                    )
//            }
        }
        return notificationBuilder.build()
    }


}