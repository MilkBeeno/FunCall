package com.milk.funcall.ad.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.milk.funcall.R

class HomeAdLayout : FrameLayout {
    // 谷歌广告资源
    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null
    private var mediaView: MediaView? = null
    private var callToActionView: Button? = null
    private var iconView: ImageView? = null
    private var primaryView: TextView? = null
    private var secondaryView: TextView? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.layout_home_ad, this, true)
    }

    fun setNativeAd(nativeAd: NativeAd) {
        initializeView()
        this.nativeAd = nativeAd
        val store: String? = nativeAd.store
        val advertiser: String? = nativeAd.advertiser
        val headline: String? = nativeAd.headline
        val cta: String? = nativeAd.callToAction
        val icon: NativeAd.Image? = nativeAd.icon

        nativeAdView?.mediaView = mediaView
        nativeAdView?.callToActionView = callToActionView
        nativeAdView?.headlineView = primaryView

        primaryView?.text = headline
        callToActionView?.text = cta

        val secondaryText = when {
            adHasOnlyStore(nativeAd) -> {
                nativeAdView?.storeView = secondaryView
                store.toString()
            }
            advertiser?.isNotBlank() == true -> {
                nativeAdView?.advertiserView = secondaryView
                advertiser.toString()
            }
            else -> ""
        }
        if (secondaryText.isBlank()) secondaryView?.visibility = GONE
        secondaryView?.text = secondaryText

        if (icon != null) {
            iconView?.visibility = VISIBLE
            iconView?.setImageDrawable(icon.drawable)
        } else iconView?.visibility = GONE

        nativeAdView?.setNativeAd(nativeAd)
    }

    fun destroyNativeAd() = nativeAd?.destroy()

    private fun adHasOnlyStore(nativeAd: NativeAd): Boolean {
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
    }

    private fun initializeView() {
        if (nativeAdView == null) {
            nativeAdView = findViewById(R.id.nativeAdView)
            mediaView = findViewById(R.id.mediaView)
            callToActionView = findViewById(R.id.callToActionView)
            iconView = findViewById(R.id.iconView)
            primaryView = findViewById(R.id.primaryView)
            secondaryView = findViewById(R.id.secondary)
        }
    }
}