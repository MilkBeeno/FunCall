package com.milk.funcall.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdManager
import com.milk.funcall.ad.AdSwitch
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine

class MainViewModel : ViewModel() {
    private var firstHomePageAd = MutableSharedFlow<NativeAd?>()
    private var secondHomePageAd = MutableSharedFlow<NativeAd?>()
    private var thirdHomePageAd = MutableSharedFlow<NativeAd?>()

    // 三个广告请求的状态
    internal val homePageAdLoadSuccess =
        combine(thirdHomePageAd, secondHomePageAd, firstHomePageAd) { a, b, c -> arrayOf(a, b, c) }

    internal fun loadNativeAd(context: Context) {
        getFirstHomePageAd(context)
        getSecondHomePageAd(context)
        getThirdHomePageAd(context)
    }

    private fun getFirstHomePageAd(context: Context) {
        ioScope {
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_FIRST)
            if (adUnitId.isNotBlank() && AdSwitch.homeListFirst)
                AdManager.loadNativeAds(
                    context = context,
                    adUnitId = adUnitId,
                    failedRequest = { ioScope { firstHomePageAd.emit(null) } },
                    successRequest = { ioScope { firstHomePageAd.emit(it) } })
            else {
                delay(500)
                firstHomePageAd.emit(null)
            }
        }
    }

    private fun getSecondHomePageAd(context: Context) {
        ioScope {
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_SECOND)
            if (adUnitId.isNotBlank() && AdSwitch.homeListSecond)
                AdManager.loadNativeAds(
                    context = context,
                    adUnitId = adUnitId,
                    failedRequest = { ioScope { secondHomePageAd.emit(null) } },
                    successRequest = { ioScope { secondHomePageAd.emit(it) } })
            else {
                delay(500)
                secondHomePageAd.emit(null)
            }
        }
    }

    private fun getThirdHomePageAd(context: Context) {
        ioScope {
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_THIRD)
            if (adUnitId.isNotBlank() && AdSwitch.homeListThird)
                AdManager.loadNativeAds(
                    context = context,
                    adUnitId = adUnitId,
                    failedRequest = { ioScope { thirdHomePageAd.emit(null) } },
                    successRequest = { ioScope { thirdHomePageAd.emit(it) } })
            else {
                delay(500)
                thirdHomePageAd.emit(null)
            }
        }
    }

    internal fun getConversationCount() = MessageRepository.getConversationCount()
}