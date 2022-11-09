package com.milk.funcall.user.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.ATNativeDislikeListener
import com.anythink.nativead.api.ATNativeEventListener
import com.anythink.nativead.api.NativeAd
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.user.status.ItemAdType
import com.milk.simple.ktx.ioScope

/** 原生广告 分为 google原生  Facebook 原生， is原生 */
class ListAdView : FrameLayout {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    private var anyThinkNativeAdView: ATNativeAdView? = null
    private var mNativeAdTop: NativeAd? = null

   internal fun showTopOnNativeAd(itemAdType: ItemAdType) {
        removeAllViews()
        visibility = VISIBLE
        anyThinkNativeAdView?.removeAllViews()
        anyThinkNativeAdView = ATNativeAdView(context)
        anyThinkNativeAdView?.let {
            if (it.parent == null) {
                val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                addView(it, params)
            }
        }
        mNativeAdTop?.destory()
        dispatchItemAd(itemAdType) { nativeAd ->
            mNativeAdTop = nativeAd
            mNativeAdTop?.let {
                it.setNativeEventListener(object : ATNativeEventListener {
                    override fun onAdImpressed(view: ATNativeAdView, atAdInfo: ATAdInfo) {

                    }

                    override fun onAdClicked(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                        when (itemAdType) {
                            ItemAdType.FirstAd ->
                                FireBaseManager.logEvent(FirebaseKey.CLICK_AD_1)
                            ItemAdType.SecondAd ->
                                FireBaseManager.logEvent(FirebaseKey.CLICK_AD_2)
                            ItemAdType.ThirdAd ->
                                FireBaseManager.logEvent(FirebaseKey.CLICK_AD_3)
                            else -> Unit
                        }
                    }

                    override fun onAdVideoStart(view: ATNativeAdView) {
                        when (itemAdType) {
                            ItemAdType.FirstAd ->
                                FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_1)
                            ItemAdType.SecondAd ->
                                FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_2)
                            ItemAdType.ThirdAd ->
                                FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_3)
                            else -> Unit
                        }
                    }

                    override fun onAdVideoEnd(view: ATNativeAdView) {

                    }

                    override fun onAdVideoProgress(view: ATNativeAdView, progress: Int) {

                    }
                })
                it.setDislikeCallbackListener(object : ATNativeDislikeListener() {
                    override fun onAdCloseButtonClick(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                        if (view.parent != null) {
                            (view.parent as ViewGroup).removeView(view)
                        }
                    }
                })
                val selfRender = ListNativeView(context)
                selfRender.createView(it)
                try {
                    it.renderAdContainer(anyThinkNativeAdView, selfRender)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                anyThinkNativeAdView?.visibility = VISIBLE
                it.prepare(anyThinkNativeAdView, selfRender.prepareInfo)
            }
        }
    }

    private fun dispatchItemAd(type: ItemAdType, action: (NativeAd) -> Unit) {
        if (context is FragmentActivity) {
            when (type) {
                ItemAdType.FirstAd ->
                    getFirstHomePageAd(context as FragmentActivity) {
                        if (it != null) action(it)
                    }
                ItemAdType.SecondAd -> {
                    getSecondHomePageAd(context as FragmentActivity) {
                        if (it != null) action(it)
                    }
                }
                ItemAdType.ThirdAd ->
                    getThirdHomePageAd(context as FragmentActivity) {
                        if (it != null) action(it)
                    }
                else -> Unit
            }
        }
    }

    private fun getFirstHomePageAd(activity: FragmentActivity, action: (NativeAd?) -> Unit) {
        ioScope {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_1)
            val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_FIRST)
            if (adUnitId.isNotBlank()) {
                AdManager.loadNativeAd(
                    activity = activity,
                    adUnitId = adUnitId,
                    loadFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_1, adUnitId, it)
                        FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_1, adUnitId, it)
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_1)
                        action(it?.nativeAd)
                    }
                )
            }
        }
    }

    private fun getSecondHomePageAd(activity: FragmentActivity, action: (NativeAd?) -> Unit) {
        ioScope {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_2)
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_SECOND)
            if (adUnitId.isNotBlank()) {
                AdManager.loadNativeAd(
                    activity = activity,
                    adUnitId = adUnitId,
                    loadFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_2, adUnitId, it)
                        FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_2, adUnitId, it)
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_2)
                        action(it?.nativeAd)
                    }
                )
            }
        }
    }

    private fun getThirdHomePageAd(activity: FragmentActivity, action: (NativeAd?) -> Unit) {
        ioScope {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_3)
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_THIRD)
            if (adUnitId.isNotBlank()) {
                AdManager.loadNativeAd(
                    activity = activity,
                    adUnitId = adUnitId,
                    loadFailureRequest = {
                        FireBaseManager
                            .logEvent(FirebaseKey.Ad_request_failed_3, adUnitId, it)
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_SHOW_FAILED_3, adUnitId, it)
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_3)
                        action.invoke(it?.nativeAd)
                    }
                )
            }
        }
    }
}