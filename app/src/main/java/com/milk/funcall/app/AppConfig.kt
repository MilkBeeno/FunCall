package com.milk.funcall.app

import com.milk.funcall.BuildConfig
import com.milk.funcall.common.constrant.AppConfigKey
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger

object AppConfig {
    /** 免广告类型: 1->免个人主页广告 2->免 app 所有广告 */
    internal var freeAdType: Int = 0
        set(value) {
            KvManger.put(AppConfigKey.FREE_AD_TYPE, value)
            field = value
        }
        get() {
            field = KvManger.getInt(AppConfigKey.FREE_AD_TYPE)
            return field
        }

    /** 观看广告解锁个人资料信息免费次数 */
    internal var viewAdUnlockTimes: Int = 0
        set(value) {
            KvManger.put(AppConfigKey.VIEW_AD_UNLOCK_TIMES, value)
            field = value
        }
        get() {
            field = KvManger.getInt(AppConfigKey.VIEW_AD_UNLOCK_TIMES)
            return field
        }

    /** 免费解锁个人资料信息次数 */
    internal var freeUnlockTimes: Int = 3
        set(value) {
            KvManger.put(AppConfigKey.FREE_UNLOCK_TIMES, value)
            field = value
        }
        get() {
            field = KvManger.getInt(AppConfigKey.FREE_UNLOCK_TIMES)
            return field
        }

    /** 订阅 VIP 折扣数量 */
    internal var discountNumber: Int = 0
        set(value) {
            KvManger.put(AppConfigKey.SUBSCRIBE_DISCOUNT_VALUE, value)
            field = value
        }
        get() {
            field = KvManger.getInt(AppConfigKey.SUBSCRIBE_DISCOUNT_VALUE)
            return field
        }

    internal fun obtain() {
        ioScope {
            val apiResponse = AppRepository().getConfig(
                BuildConfig.AD_APP_ID,
                BuildConfig.AD_APP_VERSION,
                BuildConfig.AD_APP_CHANNEL
            )
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                try {
                    apiResult[AppConfigKey.FREE_AD_TYPE]?.let {
                        freeAdType = it.toInt()
                    }
                    apiResult[AppConfigKey.VIEW_AD_UNLOCK_TIMES]?.let {
                        viewAdUnlockTimes = it.toInt()
                    }
                    apiResult[AppConfigKey.FREE_UNLOCK_TIMES]?.let {
                        freeUnlockTimes = it.toInt()
                    }
                    apiResult[AppConfigKey.SUBSCRIBE_DISCOUNT_VALUE]?.let {
                        discountNumber = it.toInt()
                    }
                } catch (e: NumberFormatException) {
                    Logger.d("类型转换错误信息:${e.message}", "AppConfig")
                    e.printStackTrace()
                }
            }
        }
    }
}