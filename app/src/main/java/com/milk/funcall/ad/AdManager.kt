package com.milk.funcall.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
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
        failedRequest: (String) -> Unit = {},
        successRequest: () -> Unit = {}
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    failedRequest(adError.message)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    AdManager.interstitialAd = interstitialAd
                    successRequest()
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

    /** 查看横幅广告 */
    fun loadBannerAd(
        context: Context,
        adUnitId: String,
        failedRequest: (String) -> Unit = {},
        successRequest: () -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        clickRequest: () -> Unit = {},
    ): AdView {
        val adView = AdView(context)
        adView.adUnitId = adUnitId
        adView.setAdSize(AdSize.BANNER)
        val adRequest = AdRequest.Builder().build()
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                successRequest()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                clickRequest()
            }

            override fun onAdOpened() {
                super.onAdOpened()
                showSuccessRequest()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                failedRequest(p0.message)
            }
        }
        adView.loadAd(adRequest)
        return adView
    }

    fun loadIncentiveVideoAd(
        context: Context,
        adUnitId: String,
        loadFailedRequest: (String) -> Unit = {},
        loadSuccessRequest: (RewardedAd) -> Unit = {},
        showFailedRequest: (String) -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        clickRequest: () -> Unit = {},
    ) {
        val adRequest = AdRequest.Builder().build()
        val fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                showFailedRequest(p0.message)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                showSuccessRequest()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                clickRequest()
            }
        }
        RewardedAd.load(context, adUnitId, adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadFailedRequest(p0.message)
                }

                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    p0.fullScreenContentCallback = fullScreenContentCallback
                    loadSuccessRequest(p0)
                }
            })
    }
}