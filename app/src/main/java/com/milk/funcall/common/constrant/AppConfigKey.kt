package com.milk.funcall.common.constrant

object AppConfigKey {
    /** 免广告类型 */
    const val FREE_AD_TYPE = "adCancelType"

    /** 查看个人资料广告解锁最大次数 */
    const val VIEW_AD_UNLOCK_TIMES = "viewAdUnlockTimes"

    /** 查看个人资料免费解锁最大次数 */
    const val FREE_UNLOCK_TIMES = "freeUnlockTimes"

    /** 订阅的折扣数值 */
    const val SUBSCRIBE_DISCOUNT_VALUE = "Annual_subscription_discount_rate"

    /** 年订阅商品 ID */
    const val PRODUCT_ID_OF_YEAR = "Subscribe_product_info_year_id"

    /** 周订阅商品 ID */
    const val PRODUCT_ID_OF_WEEK = "Subscribe_product_info_week_id"

    /** 年订阅折扣商品 ID */
    const val PRODUCT_ID_OF_YEAR_DISCOUNT = "Promotional_product_info_year_discount_id"

    /** 年订阅折扣商品百分比 */
    const val PRODUCT_SCALE_OF_YEAR_DISCOUNT = "Promotional_product_info_year_discount_number"

    /** 年订阅折扣商品 -- 原商品 ID */
    const val PRODUCT_ID_OF_YEAR_ORIGIN = "Promotional_product_info_year_origin_id"

    /** 是否展示折扣商品弹窗 1—>展示  0—>不展示 默认是展示的 */
    const val SHOW_DISCOUNT_PRODUCT_DIALOG = "Subscribe_to_open_promotions"

    /** 每次看广告可匹配次数 */
    const val MATCH_AD_REWARD_TIME = "matc_AD_reward_time"

    /** 免费匹配次数 */
    const val FREE_MATCH_TIMES = "free_match_times"
}