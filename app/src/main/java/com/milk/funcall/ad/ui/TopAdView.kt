package com.milk.funcall.ad.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.ATNativeDislikeListener
import com.anythink.nativead.api.ATNativeEventListener
import com.anythink.nativead.api.NativeAd

/**
 * 原生广告  分为 google原生  Facebook 原生， is原生
 */
class TopAdView : FrameLayout {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    // topOn 原生广告
    private var anyThinkNativeAdView: ATNativeAdView? = null
    private var mNativeAdTop: NativeAd? = null

    fun showTopOnNativeAd(nativeAd: NativeAd) {
        visibility = VISIBLE
        anyThinkNativeAdView = ATNativeAdView(context)
        anyThinkNativeAdView?.let {
            it.removeAllViews()
            if (it.parent == null) {
                val params = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
                addView(it, params)
            }
        }
        mNativeAdTop?.destory()
        mNativeAdTop = nativeAd
        mNativeAdTop?.let {
            it.setNativeEventListener(object : ATNativeEventListener {
                override fun onAdImpressed(view: ATNativeAdView, atAdInfo: ATAdInfo) {

                }

                override fun onAdClicked(view: ATNativeAdView, atAdInfo: ATAdInfo) {

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
            val selfRender = TopNativeView(context)
            selfRender.createView(nativeAd)
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