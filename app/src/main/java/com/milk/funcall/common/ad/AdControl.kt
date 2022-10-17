package com.milk.funcall.common.ad

import com.milk.funcall.BuildConfig
import com.milk.funcall.common.ad.repo.AdRepository
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger

/** 广告开关控制器、控制某个广告位是否展示或关闭 */
object AdControl {
    var homeListFirst: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.HOMEPAGE_WATERFALL_ADS_FIRST, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.HOMEPAGE_WATERFALL_ADS_FIRST)
            return field
        }
    var homeListSecond: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.HOMEPAGE_WATERFALL_ADS_SECOND, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.HOMEPAGE_WATERFALL_ADS_SECOND)
            return field
        }
    var homeListThird: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.HOMEPAGE_WATERFALL_ADS_THIRD, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.HOMEPAGE_WATERFALL_ADS_THIRD)
            return field
        }

    fun obtain() {
        ioScope {
            val apiResponse = AdRepository().getAdSwitch(
                BuildConfig.AD_APP_ID,
                BuildConfig.AD_APP_VERSION,
                BuildConfig.AD_APP_CHANNEL
            )
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                apiResult[AdCodeKey.HOMEPAGE_WATERFALL_ADS_FIRST]?.let {
                    homeListFirst = it == "true"
                }
                apiResult[AdCodeKey.HOMEPAGE_WATERFALL_ADS_SECOND]?.let {
                    homeListSecond = it == "true"
                }
                apiResult[AdCodeKey.HOMEPAGE_WATERFALL_ADS_THIRD]?.let {
                    homeListThird = it == "true"
                }
            }
        }
    }
}