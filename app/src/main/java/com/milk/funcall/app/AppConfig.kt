package com.milk.funcall.app

import com.milk.funcall.common.constrant.AppConfigKey

object AppConfig {

    /** app 初始配置全部保存在内存中 */
    private val current = mutableMapOf<String, String>()

    /** 免广告类型: 1->免个人主页广告 2->免 app 所有广告 */
    internal val freeAdType: Int = 0
        get() {
            return try {
                current[AppConfigKey.FREE_AD_TYPE]?.toInt() ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
                field
            }
        }

    /** 观看广告解锁个人资料信息免费次数 */
    internal val viewAdUnlockTimes: Int = 0
        get() {
            return try {
                current[AppConfigKey.VIEW_AD_UNLOCK_TIMES]?.toInt() ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
                field
            }
        }

    /** 免费解锁个人资料信息次数 */
    internal val freeUnlockTimes: Int = 0
        get() {
            return try {
                current[AppConfigKey.FREE_UNLOCK_TIMES]?.toInt() ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
                field
            }
        }

    /** 订阅 VIP 折扣数量 */
    internal val discountNumber: Int = 0
        get() {
            return try {
                current[AppConfigKey.SUBSCRIBE_DISCOUNT_VALUE]?.toInt() ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
                field
            }
        }

    /** 年订阅商品 ID */
    internal val subsYearId: String
        get() {
            return current[AppConfigKey.PRODUCT_ID_OF_YEAR].toString()
        }

    /** 周订阅商品 ID */
    internal val subsWeekId: String
        get() {
            return current[AppConfigKey.PRODUCT_ID_OF_WEEK].toString()
        }

    /** 促销年订阅商品 ID */
    internal val subsYearDiscountId: String
        get() {
            return current[AppConfigKey.PRODUCT_ID_OF_YEAR_DISCOUNT].toString()
        }

    /** 促销年订阅商品折扣力度 */
    internal val subsYearDiscountScale: Int = 0
        get() {
            return try {
                current[AppConfigKey.PRODUCT_SCALE_OF_YEAR_DISCOUNT]?.toInt() ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
                field
            }
        }

    /** 年订阅商品折扣原商品 ID */
    internal val subsYearDiscountOriginId: String
        get() {
            return current[AppConfigKey.PRODUCT_ID_OF_YEAR_ORIGIN].toString()
        }

    /** 是否显示折扣弹窗 */
    internal val showSubsYearDiscountDialog: Boolean = false
        get() {
            return try {
                (current[AppConfigKey.SHOW_DISCOUNT_PRODUCT_DIALOG]?.toInt() ?: 0) == 1
            } catch (e: Exception) {
                e.printStackTrace()
                field
            }
        }

    internal fun save(config: MutableMap<String, String>) {
        current.clear()
        current.putAll(config)
    }
}