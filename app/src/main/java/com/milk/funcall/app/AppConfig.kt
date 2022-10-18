package com.milk.funcall.app

import com.milk.funcall.BuildConfig
import com.milk.funcall.common.constrant.AppConfigKey
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.safeToInt
import com.milk.simple.mdr.KvManger

object AppConfig {
    /** 免广告类型：0->需要展示广告 1->免个人主页广告 2->免 app 所有广告 */
    internal var adCancelType: Int = 0
        set(value) {
            KvManger.put(AppConfigKey.AD_CANCEL_TYPE, value)
            field = value
        }
        get() {
            field = KvManger.getInt(AppConfigKey.AD_CANCEL_TYPE)
            return field
        }

    /** 观看广告解锁个人资料信息免费次数 */
    internal var viewAdUnlockTimes: Int = 3
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

    fun obtain() {
        ioScope {
            val apiResponse = AppRepository().getConfig(
                BuildConfig.AD_APP_ID,
                BuildConfig.AD_APP_VERSION,
                BuildConfig.AD_APP_CHANNEL
            )
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                apiResult[AppConfigKey.AD_CANCEL_TYPE]?.let {
                    adCancelType = it.safeToInt()
                }
                apiResult[AppConfigKey.VIEW_AD_UNLOCK_TIMES]?.let {
                    viewAdUnlockTimes = it.safeToInt()
                }
                apiResult[AppConfigKey.FREE_UNLOCK_TIMES]?.let {
                    freeUnlockTimes = it.safeToInt()
                }
            }
        }
    }
}