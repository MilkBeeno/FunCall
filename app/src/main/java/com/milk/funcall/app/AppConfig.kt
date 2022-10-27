package com.milk.funcall.app

import com.milk.funcall.BuildConfig
import com.milk.funcall.account.Account
import com.milk.funcall.common.constrant.AppConfigKey
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger

object AppConfig {
    /** 订阅已取消，但尚未到期。仅当订阅是自动续订方案时，这个状态才可用。*/
    private const val SUBSCRIPTION_STATE_CANCELED = "SUBSCRIPTION_STATE_CANCELED"

    /** 订阅处于有效状态。- (1) 如果订阅是自动续订方案，则至少有一个项目已自动续订且未过期。
     * - (2) 如果订阅是预付费方案，至少有一项不会过期。*/
    private const val SUBSCRIPTION_STATE_ACTIVE = "SUBSCRIPTION_STATE_ACTIVE"

    /** 订阅已确认 */
    private const val ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED = "ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED"

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
                } catch (e: NumberFormatException) {
                    Logger.d("类型转换错误信息:${e.message}", "AppConfig")
                    e.printStackTrace()
                }
            }
        }
    }

    internal fun getSubscribeStatus(productId: String = "", purchaseToken: String = "") {
        if (Account.userLogged) {
            ioScope {
                val apiResponse =
                    AppRepository().getSubscribeStatus(productId, purchaseToken)
                val apiResult = apiResponse.data
                if (apiResponse.success && apiResult != null) {
                    Account.userSubscribe =
                        (apiResult.subscriptionState == SUBSCRIPTION_STATE_CANCELED
                            || apiResult.subscriptionState == SUBSCRIPTION_STATE_ACTIVE) &&
                            apiResult.acknowledgementState == ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED
                }
            }
        }
    }
}