package com.milk.funcall.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FireBaseManager {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun initialize(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun logEvent(name: String, params: Bundle? = null) {
        if (this::firebaseAnalytics.isInitialized) {
            firebaseAnalytics.logEvent(name, params)
        }
    }
}