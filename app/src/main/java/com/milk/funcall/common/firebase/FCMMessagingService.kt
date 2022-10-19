package com.milk.funcall.common.firebase

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.milk.funcall.BaseApplication
import com.milk.funcall.common.util.BitmapUtil
import com.milk.funcall.common.util.NotificationUtil
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger

class FCMMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notification = message.notification
        if (notification != null && notification.body != null) {
            sendNotification(
                BaseApplication.instance,
                notification.title.toString(),
                notification.body.toString(),
                notification.icon.toString(),
            )
        } else {
            sendNotification(
                BaseApplication.instance,
                message.data["title"].toString(),
                message.data["body"].toString(),
                message.data["icon"].toString()
            )
        }
    }

    private fun sendNotification(
        context: Context,
        messageTitle: String,
        messageBody: String,
        imageUrl: String
    ) {
        Logger.d(
            "MessageTitle=$messageTitle,MessageBody=$messageBody",
            "FCMMessagingService"
        )
        // todo hlc 参数配置问题
        ioScope {
            val bitmap = BitmapUtil.obtain(imageUrl)
            if (bitmap != null) {
                NotificationUtil.show(context, 12345, messageTitle, messageBody, bitmap)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}