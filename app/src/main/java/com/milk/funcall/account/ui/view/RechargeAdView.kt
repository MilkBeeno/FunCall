package com.milk.funcall.account.ui.view

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
import com.milk.funcall.user.ui.view.ListNativeView
import com.milk.simple.ktx.ioScope

/** 原生广告 分为 google原生  Facebook 原生， is原生 */
class RechargeAdView : FrameLayout {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    private var anyThinkNativeAdView: ATNativeAdView? = null
    private var mNativeAdTop: NativeAd? = null

    fun showTopOnNativeAd() {
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
        getRechargePageAd { nativeAd ->
            mNativeAdTop = nativeAd
            mNativeAdTop?.let {
                it.setNativeEventListener(object : ATNativeEventListener {
                    override fun onAdImpressed(view: ATNativeAdView, atAdInfo: ATAdInfo) {

                    }

                    override fun onAdClicked(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_AD_7)
                    }

                    override fun onAdVideoStart(view: ATNativeAdView) {

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

    private fun getRechargePageAd(action: (NativeAd?) -> Unit) {
        ioScope {
            if (context is FragmentActivity) {
                val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.RECHARGE_NATIVE_AD)
                if (adUnitId.isNotBlank()) {
                    FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_7)
                    AdManager.loadNativeAd(
                        activity = context as FragmentActivity,
                        adUnitId = adUnitId,
                        loadFailureRequest = {
                            FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_7)
                        },
                        loadSuccessRequest = {
                            FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_7)
                            action(it?.nativeAd)
                        }
                    )
                }
            }
        }
    }
}