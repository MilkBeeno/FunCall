package com.milk.funcall.common.pay

import android.app.Activity

interface Pay {
    /** SDK 初始化 */
    fun initialize(activity: Activity)

    /** 支付断开连接 */
    fun disconnect()

    /** 支付连接成功 */
    fun connected()

    /** 进行购买 */
    fun launchPurchase(activity: Activity, productDetails: Any)

    /** 购买成功 */
    fun paySuccessListener(listener: (String, String) -> Unit)

    /** 取消购买 */
    fun payCancelListener(listener: () -> Unit)

    /** 购买失败 */
    fun payFailureListener(listener: () -> Unit)

    fun finishConnection()
}