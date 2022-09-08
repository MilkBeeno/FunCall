package com.milk.funcall.ad.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.setMargins
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.ATNativeDislikeListener
import com.anythink.nativead.api.ATNativeEventListener
import com.anythink.nativead.api.NativeAd

/**
 * 原生广告  分为 google原生  Facebook 原生， is原生
 */
class TopAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // topOn 原生广告
    private var anyThinkNativeAdView: ATNativeAdView? = null
    private var mNativeAdTop: NativeAd? = null

    fun showTopOnNativeAd(nativeAd: NativeAd, scenario: String) {
        anyThinkNativeAdView = ATNativeAdView(context)
        // ATNative.entryAdScenario(AdConfigUtils.topNativeAd, scenario)
        visibility = VISIBLE
        var title = "home_native"
        anyThinkNativeAdView?.let {
            it.removeAllViews()
            if (it.parent == null) {
                val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                params.setMargins(40)
                addView(it, params)
            }
        }
        mNativeAdTop?.destory()
        mNativeAdTop = nativeAd
        mNativeAdTop?.let {
            it.setNativeEventListener(object : ATNativeEventListener {
                override fun onAdImpressed(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                    val advName = when (atAdInfo.networkFirmId) {
                        2 -> {
                            "admob"
                        }
                        1 -> {
                            "fb"
                        }
                        else -> {
                            "topOn"
                        }
                    }
                }

                override fun onAdClicked(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                    Log.e("aaa", "native ad onAdClicked:\n$atAdInfo")
                }

                override fun onAdVideoStart(view: ATNativeAdView) {
                    Log.e("aaa", "native ad onAdVideoStart")
                }

                override fun onAdVideoEnd(view: ATNativeAdView) {
                    Log.e("aaa", "native ad onAdVideoEnd")
                }

                override fun onAdVideoProgress(view: ATNativeAdView, progress: Int) {
                    Log.e("aaa", "native ad onAdVideoProgress:$progress")
                }
            })
            it.setDislikeCallbackListener(object : ATNativeDislikeListener() {
                override fun onAdCloseButtonClick(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                    Log.i("aaa", "native ad onAdCloseButtonClick")
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

            }
            anyThinkNativeAdView?.visibility = VISIBLE
            it.prepare(anyThinkNativeAdView, selfRender.prepareInfo)
        }
    }
}