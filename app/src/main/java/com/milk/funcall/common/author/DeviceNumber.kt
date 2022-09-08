package com.milk.funcall.common.author

import android.content.Context
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.milk.funcall.common.constrant.EventKey
import com.milk.simple.mdr.KvManger
import java.util.*
import java.util.concurrent.Executors

object DeviceNumber {

    internal fun obtain(context: Context, resultRequest: (Boolean, String) -> Unit) {
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

                        resultRequest(true, getDeviceUniqueId())
                    }
                }, Executors.newSingleThreadExecutor()
            )
        } else resultRequest(true, getDeviceUniqueId())
    }

    private fun getDeviceUniqueId(): String {
        val lastDeviceUniqueId = KvManger.getString(EventKey.LOGIN_DEVICE_UNIQUE_ID)
        val stringRandom = lastDeviceUniqueId.ifBlank {
            val deviceUniqueId =
                System.currentTimeMillis().toString().plus(getStringRandom())
            KvManger.put(EventKey.LOGIN_DEVICE_UNIQUE_ID, deviceUniqueId)
            deviceUniqueId
        }
        return stringRandom
    }

    // 生成4位随机数字+字母,
    private fun getStringRandom(): String {
        var value: String = ""
        val random = Random()
        // 参数length，表示生成几位随机数
        for (i in 0 until 16) {
            val charOrNum = if (random.nextInt(2) % 2 == 0) "char" else "num"
            // 输出字母还是数字
            if ("char".equals(charOrNum, ignoreCase = true)) {
                // 输出是大写字母还是小写字母
                val temp = if (random.nextInt(2) % 2 == 0) 65 else 97
                value += (random.nextInt(26) + temp).toChar()
            } else if ("num".equals(charOrNum, ignoreCase = true)) {
                value += java.lang.String.valueOf(random.nextInt(10))
            }
        }
        return value
    }
}