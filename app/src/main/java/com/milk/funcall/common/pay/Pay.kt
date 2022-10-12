package com.milk.funcall.common.pay

import android.app.Activity
import android.content.Context

interface Pay {
    /** SDK 初始化 */
    fun initialize(context: Context)

    /** 支付断开连接 */
    fun disconnect()

    /** 支付连接成功 */
    fun connected()

    /** 进行购买 */
    fun launchPurchase(activity: Activity, productDetails: Any?)

    /** 购买成功 */
    fun purchaseSuccess(purchaseToken:Any)

    /** 取消购买 */
    fun purchaseCancel()

    /** 购买失败 */
    fun purchaseFailure()
}