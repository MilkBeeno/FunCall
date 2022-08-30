package com.milk.funcall.login.ui.vm

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Base64
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdManager
import com.milk.funcall.ad.AdSwitch
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.ad.ui.AdLoadType
import com.milk.funcall.common.timer.MilkTimer
import com.milk.funcall.firebase.FireBaseManager
import com.milk.funcall.firebase.constant.FirebaseKey
import com.milk.simple.log.Logger
import java.security.MessageDigest

class LaunchViewModel : ViewModel() {
    /** 广告加载的状态 */
    private var adLoadStatus = AdLoadType.Loading

    /** 加载广告并设置广告状态 */
    internal fun loadLaunchAd(activity: FragmentActivity, finished: () -> Unit) {
        val adUnitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.APP_START)
        MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnTickListener { t, it ->
                if (it <= 7000 && adLoadStatus == AdLoadType.Success)
                    t.finish()
            }
            .setOnFinishedListener {
                when (adLoadStatus) {
                    AdLoadType.Success -> {
                        AdManager.showInterstitial(
                            activity = activity,
                            showFailedRequest = {
                                FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED)
                                finished()
                            },
                            showSuccessRequest = {
                                FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS)
                            },
                            clickRequest = {
                                FireBaseManager.logEvent(FirebaseKey.CLICK_AD)
                            },
                            showFinishedRequest = {
                                finished()
                            })
                    }
                    else -> finished()
                }
            }
            .build()
            .start()
        if (AdSwitch.appLaunch) {
            if (adUnitId.isNotBlank()) {
                FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST)
                AdManager.loadInterstitial(activity, adUnitId,
                    loadFailedRequest = {
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_REQUEST_FAILED, adUnitId, it)
                        adLoadStatus = AdLoadType.Failure
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED)
                        adLoadStatus = AdLoadType.Success
                    })
            } else adLoadStatus = AdLoadType.Failure
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