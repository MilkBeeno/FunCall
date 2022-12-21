package com.milk.funcall.common.pay

import android.app.Activity
import android.content.Context

interface Pay {
    /** 支付服务初始化 */
    fun initialize(context: Context)

    /** 支付服务连接失败 */
    fun connectFailed()

    /** 支付服务连接成功 */
    fun connectSucceeded()

    /** 断开谷歌支付连接 */
    fun disconnect()

    /** 查询商品详情 */
    fun queryProducts()

    /** 进行商品购买 */
    fun payProduct(activity: Activity, productId: String)

    /** 购买失败 */
    fun payFailed(request: () -> Unit)

    /** 取消购买 */
    fun payCanceled(request: () -> Unit)

    /** 购买成功 */
    fun paySucceeded(request: (String, String) -> Unit)

    /** 获取支付对象工厂 */
    abstract class Factory {
        abstract fun create(): Pay
    }
}