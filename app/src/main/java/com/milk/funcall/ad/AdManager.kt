package com.milk.funcall.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.funcall.BuildConfig
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.firebase.FireBaseManager
import com.milk.funcall.firebase.constant.FirebaseKey

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
        onFailedRequest: () -> Unit = {},
        onSuccessRequest: (String) -> Unit = {}
    ) {
        FireBaseManager
            .logEvent(FirebaseKey.MAKE_AN_AD_REQUEST, adUnitId, adUnitId)
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, adUnitId, adError.message)
                    interstitialAd = null
                    onFailedRequest()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, adUnitId, adUnitId)
                    AdManager.interstitialAd = interstitialAd
                    onSuccessRequest(adUnitId)
                }
            })
    }

    /** 显示插页广告 */
    fun showInterstitial(
        activity: Activity,
        adUnitId: String,
        onFailedRequest: (String) -> Unit = {},
        onSuccessRequest: () -> Unit = {},
        onFinishedRequest: () -> Unit = {}
    ) {
        if (interstitialAd == null) {
            onFailedRequest("")
            onFinishedRequest()
        } else {
            interstitialAd?.show(activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_AD, adUnitId, adUnitId)
                }

                override fun onAdDismissedFullScreenContent() {
                    onSuccessRequest()
                    onFinishedRequest()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    onFailedRequest(p0.message)
                    onFinishedRequest()
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                }
            }
        }
    }

    /** 加载原生广告 */
    fun loadNativeAds(
        context: Context,
        adUnitId: String,
        failedRequest: () -> Unit = {},
        successRequest: (NativeAd) -> Unit = {}
    ) {
        FireBaseManager
            .logEvent(FirebaseKey.MAKE_AN_AD_REQUEST, adUnitId, adUnitId)
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd {
                FireBaseManager
                    .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, adUnitId, adUnitId)
                successRequest(it)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, adUnitId, p0.message)
                    failedRequest()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_AD, adUnitId, adUnitId)
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}