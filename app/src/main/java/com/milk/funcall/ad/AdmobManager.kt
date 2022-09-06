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

object AdmobManager {
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
        loadFailedRequest: (String) -> Unit = {},
        loadSuccessRequest: () -> Unit = {}
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    loadFailedRequest(adError.message)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    AdmobManager.interstitialAd = interstitialAd
                    loadSuccessRequest()
                }
            })
    }

    /** 显示插页广告 */
    fun showInterstitial(
        activity: Activity,
        showFailedRequest: (String) -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        clickRequest: () -> Unit = {},
        showFinishedRequest: () -> Unit = {}
    ) {
        if (interstitialAd == null) {
            showFailedRequest("InterstitialAd is NULL !")
            showFinishedRequest()
        } else {
            interstitialAd?.show(activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                    clickRequest()
                }

                override fun onAdDismissedFullScreenContent() {
                    showFinishedRequest()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    showFailedRequest(p0.message)
                    showFinishedRequest()
                }

                override fun onAdShowedFullScreenContent() {
                    showSuccessRequest()
                    interstitialAd = null
                }
            }
        }
    }

    /** 加载原生广告 */
    fun loadNativeAds(
        context: Context,
        adUnitId: String,
        loadFailedRequest: (String) -> Unit = {},
        loadSuccessRequest: (NativeAd) -> Unit = {},
        showSuccessRequest: () -> Unit,
        clickRequest: () -> Unit = {},
    ) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { loadSuccessRequest(it) }
            .withAdListener(object : AdListener() {
                override fun onAdOpened() {
                    super.onAdOpened()
                    showSuccessRequest()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadFailedRequest(p0.message)
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
        loadFailedRequest: (String) -> Unit = {},
        loadSuccessRequest: () -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        clickRequest: () -> Unit = {},
    ): AdView {
        val adView = AdView(context)
        adView.adUnitId = adUnitId
        adView.setAdSize(AdSize.BANNER)
        val adRequest = AdRequest.Builder().build()
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                loadFailedRequest(p0.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                loadSuccessRequest()
            }

            override fun onAdOpened() {
                super.onAdOpened()
                showSuccessRequest()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                clickRequest()
            }
        }
        adView.loadAd(adRequest)
        return adView
    }

    /** 激励视频广告展示 */
    fun loadIncentiveVideoAd(
        activity: Activity,
        adUnitId: String,
        loadFailedRequest: (String) -> Unit = {},
        loadSuccessRequest: () -> Unit = {},
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
        RewardedAd.load(activity, adUnitId, adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadFailedRequest(p0.message)
                }

                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    loadSuccessRequest()
                    p0.fullScreenContentCallback = fullScreenContentCallback
                    p0.show(activity) {}
                }
            })
    }
}