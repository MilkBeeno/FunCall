package com.milk.funcall.ad

import com.milk.funcall.BuildConfig
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.ad.repo.AdRepository
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger

object AdSwitch {
    var adMaster: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.AD_MASTER_SWITCH, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.AD_MASTER_SWITCH)
            return field
        }
    var appLaunch: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.APP_LAUNCH_INTERSTITIAL, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.APP_LAUNCH_INTERSTITIAL)
            return field
        }
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
    var homeBanner: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.BANNER_OF_HOMEPAGE, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.BANNER_OF_HOMEPAGE)
            return field
        }
    var viewUserLink: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.VIEW_CONTACT_VIDEO_ADS, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.VIEW_CONTACT_VIDEO_ADS)
            return field
        }
    var viewUserVideo: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.VIEW_USER_VIDEO_ADS, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.VIEW_USER_VIDEO_ADS)
            return field
        }
    var viewUserImage: Boolean = true
        set(value) {
            KvManger.put(AdCodeKey.VIEW_PHOTO_ADS, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(AdCodeKey.VIEW_PHOTO_ADS)
            return field
        }

    /** 广告展示开启的开关 */
    fun obtain() {
        ioScope {
            val apiResponse = AdRepository().getAdSwitch(
                BuildConfig.AD_APP_ID,
                BuildConfig.AD_APP_VERSION,
                BuildConfig.AD_APP_CHANNEL
            )
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                apiResult[AdCodeKey.AD_MASTER_SWITCH]?.let {
                    adMaster = it == "true"
                }
                apiResult[AdCodeKey.APP_LAUNCH_INTERSTITIAL]?.let {
                    appLaunch = it == "true"
                }
                apiResult[AdCodeKey.HOMEPAGE_WATERFALL_ADS_FIRST]?.let {
                    homeListFirst = it == "true"
                }
                apiResult[AdCodeKey.HOMEPAGE_WATERFALL_ADS_SECOND]?.let {
                    homeListSecond = it == "true"
                }
                apiResult[AdCodeKey.HOMEPAGE_WATERFALL_ADS_THIRD]?.let {
                    homeListThird = it == "true"
                }
                apiResult[AdCodeKey.BANNER_OF_HOMEPAGE]?.let {
                    homeBanner = it == "true"
                }
                apiResult[AdCodeKey.VIEW_CONTACT_VIDEO_ADS]?.let {
                    viewUserLink = it == "true"
                }
                apiResult[AdCodeKey.VIEW_USER_VIDEO_ADS]?.let {
                    viewUserVideo = it == "true"
                }
                apiResult[AdCodeKey.VIEW_PHOTO_ADS]?.let {
                    viewUserImage = it == "true"
                }
            }
        }
    }
}