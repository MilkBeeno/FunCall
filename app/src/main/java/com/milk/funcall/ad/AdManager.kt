package com.milk.funcall.ad

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.milk.funcall.BuildConfig
import com.milk.funcall.ad.constant.AdCodeKey

object AdManager {
    fun initialize(context: Context) {
        MobileAds.initialize(context) {
            if (BuildConfig.DEBUG) {
                MobileAds.setRequestConfiguration(
                    RequestConfiguration
                        .Builder()
                        .setTestDeviceIds(AdCodeKey.testDeviceIds)
                        .build()
                )
            }
        }
    }
}