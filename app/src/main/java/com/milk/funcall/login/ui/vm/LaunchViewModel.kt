package com.milk.funcall.login.ui.vm

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Base64
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdManager
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.ad.ui.AdLoadType
import com.milk.simple.log.Logger
import java.security.MessageDigest

class LaunchViewModel : ViewModel() {
    /** 广告加载的状态 */
    private var adLoadStatus = AdLoadType.Loading

    /** 第一个动画已经播放完成 */
    private var firstAnimFinished: Boolean = false

    internal fun loadLaunchAd(
        activity: FragmentActivity,
        failure: () -> Unit,
        success: () -> Unit
    ) {
        adLoadStatus = AdLoadType.Loading
        val adUnitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.APP_START)
        when {
            adUnitId.isNotBlank() -> {
                AdManager.loadInterstitial(activity, adUnitId,
                    onFailedRequest = {
                        if (firstAnimFinished)
                            failure()
                        else
                            adLoadStatus = AdLoadType.Failure
                    },
                    onSuccessRequest = {
                        if (firstAnimFinished) {
                            AdManager.showInterstitial(
                                activity = activity,
                                onFailedRequest = { failure() },
                                onSuccessRequest = { success() })
                        } else adLoadStatus = AdLoadType.Success
                    })
            }
            firstAnimFinished -> failure()
            else -> adLoadStatus = AdLoadType.Failure
        }
    }

    internal fun showLaunchAd(
        activity: FragmentActivity,
        loading: () -> Unit = {},
        failure: () -> Unit,
        success: () -> Unit
    ) {
        firstAnimFinished = true
        when (adLoadStatus) {
            AdLoadType.Loading -> loading()
            AdLoadType.Failure -> failure()
            AdLoadType.Success -> {
                AdManager.showInterstitial(
                    activity = activity,
                    onFailedRequest = { failure() },
                    onSuccessRequest = { success() })
            }
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    internal fun getHasKey(activity: FragmentActivity) {
        try {
            val info = activity.packageManager
                .getPackageInfo(activity.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Logger.d(
                    "包名是" + activity.packageName +
                            "密钥是：" + Base64.encodeToString(md.digest(), Base64.DEFAULT),
                    "KeyHash"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}