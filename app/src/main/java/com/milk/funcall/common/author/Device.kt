package com.milk.funcall.common.author

import android.content.Context
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.util.concurrent.Executors

object Device {
    fun obtain(context: Context, resultRequest: (Boolean, String) -> Unit) {
        if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
            val advertisingIdInfoListenableFuture =
                AdvertisingIdClient.getAdvertisingIdInfo(context)
            Futures.addCallback(
                advertisingIdInfoListenableFuture,
                object : FutureCallback<AdvertisingIdInfo> {
                    override fun onSuccess(adInfo: AdvertisingIdInfo?) {
                        resultRequest(true, adInfo?.id.toString())
                    }

                    override fun onFailure(t: Throwable) {
                        resultRequest(true, "")
                    }
                }, Executors.newSingleThreadExecutor()
            )
        } else resultRequest(true, "")
    }
}