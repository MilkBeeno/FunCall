package com.milk.funcall.login.ui.vm

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Base64
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.anythink.splashad.api.ATSplashAd
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdLoadType
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.author.DeviceManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.timer.MilkTimer
import com.milk.funcall.login.repo.LoginRepository
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import java.security.MessageDigest

class LaunchViewModel : ViewModel() {
    /** 广告加载的状态 */
    private var adLoadStatus = AdLoadType.Loading

    /** 启动时上传设备信息 */
    internal fun uploadDeviceInfo() {
        val deviceId = DeviceManager.number
        val loginRepository = LoginRepository()
        ioScope { loginRepository.login(deviceId, AuthType.NULL, deviceId) }
    }

    /** 加载广告并设置广告状态 */
    internal fun loadLaunchAd(
        activity: FragmentActivity,
        viewGroup: ViewGroup,
        finished: () -> Unit
    ) {
        var splashAd: ATSplashAd? = null
        MilkTimer.Builder()
            .setMillisInFuture(13000)
            .setOnTickListener { t, it ->
                if (it <= 10000 && adLoadStatus == AdLoadType.Success) t.finish()
            }
            .setOnFinishedListener {
                if (adLoadStatus == AdLoadType.Success) {
                    splashAd?.show(activity, viewGroup)
                    activity.finish()
                } else {
                    finished()
                }
            }
            .build()
            .start()
        val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.APP_START)
        if (adUnitId.isNotBlank() && AdConfig.adCancelType != 2) {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_13)
            splashAd = AdManager.loadOpenAd(
                activity = activity,
                adUnitId = adUnitId,
                loadFailureRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED_13, adUnitId, it)
                    adLoadStatus = AdLoadType.Failure
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_13)
                    adLoadStatus = AdLoadType.Success
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_13)
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_13)
                },
                finishedRequest = {
                    finished()
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_13)
                })
        } else adLoadStatus = AdLoadType.Failure
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