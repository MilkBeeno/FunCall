package com.milk.funcall.common.author

import android.content.Context
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import androidx.lifecycle.liveData
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures.addCallback
import com.milk.simple.ktx.ioScope
import java.util.concurrent.Executors

object AdIdClient {

    fun determineAdvertisingInfo(context: Context) = liveData {
        if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
            val advertisingIdInfoListenableFuture =
                AdvertisingIdClient.getAdvertisingIdInfo(context)
            addCallback(
                advertisingIdInfoListenableFuture,
                object : FutureCallback<AdvertisingIdInfo> {
                    override fun onSuccess(adInfo: AdvertisingIdInfo?) {
                        ioScope { this@liveData.emit(value = adInfo?.id.toString()) }
                    }

                    override fun onFailure(t: Throwable) {
                        ioScope { emit(null) }
                    }
                }, Executors.newSingleThreadExecutor()
            )
        } else {
            ioScope { emit(null) }
        }
    }
}