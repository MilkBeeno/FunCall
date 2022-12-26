package com.milk.funcall.user.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.author.DeviceManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.funcall.user.status.UnlockType
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class PictureViewModel : ViewModel() {
    internal val changeUnlockStatusFlow = MutableSharedFlow<Boolean>()

    internal fun changeUnlockStatus(targetId: Long) {
        ioScope { UserInfoRepository.finishAd(UnlockType.Image, targetId) }
        ioScope {
            val apiResponse = UserInfoRepository
                .changeUnlockStatus(DeviceManager.number, UnlockType.Image, targetId)
            changeUnlockStatusFlow.emit(apiResponse.success)
        }
    }

    /** 查看个人照片插页广告加载 */
    internal fun loadImageAd(activity: FragmentActivity, failure: () -> Unit, success: () -> Unit) {
        FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_5)
        val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_IMAGE)
        if (adUnitId.isNotBlank()) {
            AdManager.loadIncentiveVideoAd(
                activity = activity,
                adUnitId = adUnitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_5, adUnitId, it)
                    failure()
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_5)
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_5, adUnitId, it)
                    failure()
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_5)
                    success()
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_5)
                }
            )
        } else failure()
    }
}