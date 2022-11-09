package com.milk.funcall.common.pay

import com.milk.funcall.account.Account
import com.milk.funcall.app.AppRepository
import com.milk.simple.ktx.ioScope

object PayManager {
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