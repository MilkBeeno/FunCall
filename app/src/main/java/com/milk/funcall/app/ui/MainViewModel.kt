package com.milk.funcall.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdManager
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class MainViewModel : ViewModel() {
    val mainAd = MutableSharedFlow<NativeAd?>()

    internal fun loadNativeAd(context: Context) {
        ioScope {
            val smallNativeAd = AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST)
            if (smallNativeAd.isNotBlank()) AdManager.loadNativeAds(context, smallNativeAd,
                failedRequest = {
                    ioScope { mainAd.emit(null) }
                },
                successRequest = {
                    ioScope { mainAd.emit(it) }
                })
        }
    }

    internal fun getConversationCount() = MessageRepository.getConversationCount()
}