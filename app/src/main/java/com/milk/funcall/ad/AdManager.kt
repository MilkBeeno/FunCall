package com.milk.funcall.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.funcall.BuildConfig
import com.milk.funcall.ad.constant.AdCodeKey

object AdManager {
    private var interstitialAd: InterstitialAd? = null

    fun initialize(context: Context) {
        MobileAds.initialize(context) {
            if (BuildConfig.DEBUG) {
                MobileAds.setRequestConfiguration(
                    RequestConfiguration
                        .Builder()
                        .setTestDeviceIds(AdCodeKey.testDeviceIds)
                        .build()
                )
            }
        }
    }

    /** 加载插页广告 */
    fun loadInterstitial(
        context: Context,
        adUnitId: String,
        failedRequest: () -> Unit = {},
        successRequest: (String) -> Unit = {}
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    failedRequest()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    AdManager.interstitialAd = interstitialAd
                    successRequest(adUnitId)
                }
            })
    }

    /** 显示插页广告 */
    fun showInterstitial(
        activity: Activity,
        failedRequest: (String) -> Unit = {},
        successRequest: () -> Unit = {},
        clickRequest: () -> Unit = {},
        finishedRequest: () -> Unit = {}
    ) {
        if (interstitialAd == null) {
            failedRequest("InterstitialAd is NULL !")
            finishedRequest()
        } else {
            interstitialAd?.show(activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    clickRequest()
                }

                override fun onAdDismissedFullScreenContent() {
                    finishedRequest()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    failedRequest(p0.message)
                    finishedRequest()
                }

                override fun onAdShowedFullScreenContent() {
                    successRequest()
                    interstitialAd = null
                }
            }
        }
    }

    /** 加载原生广告 */
    fun loadNativeAds(
        context: Context,
        adUnitId: String,
        failedRequest: (String) -> Unit = {},
        successRequest: (NativeAd) -> Unit = {},
        clickRequest: () -> Unit = {},
    ) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { successRequest(it) }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    failedRequest(p0.message)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    clickRequest()
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}