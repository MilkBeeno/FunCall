package com.milk.funcall.common.author

import android.app.Application
import android.provider.Settings
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.simple.mdr.KvManger
import java.util.*

object DeviceManager {
    internal var number: String = ""
        set(value) {
            KvManger.put(KvKey.PLATFORM_DEVICE_NUMBER, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.PLATFORM_DEVICE_NUMBER)
            return field
        }

    internal fun initialize(application: Application) {
        val androidId =
            Settings.System.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
        number = androidId.ifBlank { getGeneratedUniqueId() }
    }

    /** 随机生成的ID */
    private fun getGeneratedUniqueId(): String {
        val lastGeneratedUniqueId = KvManger.getString(EventKey.LOGIN_DEVICE_UNIQUE_ID)
        val stringRandom = lastGeneratedUniqueId.ifBlank {
            val deviceUniqueId =
                System.currentTimeMillis().toString().plus(getStringRandom())
            KvManger.put(EventKey.LOGIN_DEVICE_UNIQUE_ID, deviceUniqueId)
            deviceUniqueId
        }
        return stringRandom
    }

    /** 生成4位随机数字+字母 */
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