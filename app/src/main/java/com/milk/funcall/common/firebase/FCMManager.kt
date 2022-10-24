package com.milk.funcall.common.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.milk.simple.log.Logger

object FCMManager {
    internal fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    Logger.d(
                        "推送成功，新的Token是：${p0.result}",
                        "FirebaseMessaging"
                    )
                } else {
                    Logger.d(
                        "推送失败，错误是：${p0.exception}",
                        "FirebaseMessaging"
                    )
                }
            }
    }
}