package com.milk.funcall.common.pay

import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.app.AppRepository
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.timer.MilkTimer
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger

object PayManager {
    /** 全部弹窗计时器 */
    val timer by lazy {
        MilkTimer.Builder()
            .setCountDownInterval(1000)
            .setMillisInFuture(2 * 60 * 60 * 1000)
            .setOnTickListener { _, l ->
                LiveEventBus.get<Long>(EventKey.UPDATE_SUBSCRIBE_DISCOUNT_TIME).post(l)
            }
            .setOnFinishedListener {
                LiveEventBus.get<Long>(EventKey.UPDATE_SUBSCRIBE_DISCOUNT_TIME).post(0)
                subscribeDiscountProductTime = 0
            }
            .build()
    }

    /** 判断是否在折扣期内 */
    val isSubscribeDiscountPeriod: Boolean
        get() {
            return System.currentTimeMillis() - subscribeDiscountProductTime < 2 * 60 * 60 * 1000
                && subscribeDiscountProductTime > 0
        }

    /** 是否实在订阅优惠期内 */
    var subscribeDiscountProductTime: Long = 0
        set(value) {
            KvManger.put(KvKey.SUBSCRIBE_DISCOUNT_PRODUCT_START, value)
            field = value
        }
        get() {
            field = KvManger.getLong(KvKey.SUBSCRIBE_DISCOUNT_PRODUCT_START)
            return field
        }

    /** 谷歌支付服务实例 */
    val googlePay by lazy { GooglePlay.GoogleFactory.INSTANCE.create() as GooglePlay }

    /** 订阅已取消，但尚未到期。仅当订阅是自动续订方案时，这个状态才可用。*/
    private const val SUBSCRIPTION_STATE_CANCELED = "SUBSCRIPTION_STATE_CANCELED"

    /** 订阅处于有效状态。- (1) 如果订阅是自动续订方案，则至少有一个项目已自动续订且未过期。
     * - (2) 如果订阅是预付费方案，至少有一项不会过期。*/
    private const val SUBSCRIPTION_STATE_ACTIVE = "SUBSCRIPTION_STATE_ACTIVE"

    /** 订阅已确认 */
    private const val ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED = "ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED"

    internal fun getPayStatus(productId: String = "", purchaseToken: String = "") {
        if (Account.userLogged) {
            ioScope {
                val apiResponse =
                    AppRepository().getSubscribeStatus(productId, purchaseToken)
                val apiResult = apiResponse.data
                val state = (apiResult?.subscriptionState == SUBSCRIPTION_STATE_CANCELED
                    || apiResult?.subscriptionState == SUBSCRIPTION_STATE_ACTIVE) &&
                    apiResult.acknowledgementState == ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED
                Account.userSubscribe = state
                Account.userSubscribeFlow.emit(state)
            }
        }
    }
}