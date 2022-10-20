package com.milk.funcall.common.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.milk.simple.log.Logger

object FireBaseManager {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun initialize(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
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

    fun logEvent(name: String, key: String = "", value: String = "") {
        if (this::firebaseAnalytics.isInitialized) {
            var bundle: Bundle? = null
            if (key.isNotBlank() && value.isNotBlank()) {
                bundle = Bundle()
                bundle.putString(key, value)
            }
            firebaseAnalytics.logEvent(name, bundle)
        }
    }
}