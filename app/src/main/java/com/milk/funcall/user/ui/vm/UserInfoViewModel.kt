package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.anythink.interstitial.api.ATInterstitial
import com.milk.funcall.account.Account
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.TopOnManager
import com.milk.funcall.common.ad.constant.AdCodeKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.firebase.constant.FirebaseKey
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class UserInfoViewModel : ViewModel() {
    private var targetId: Long = 0
    private var adIsLoading = false
    internal val userInfoFlow = MutableStateFlow<UserInfoModel?>(UserInfoModel())
    internal val userFollowedStatusFlow = MutableSharedFlow<Boolean?>()
    internal var hasViewedLink: Boolean = false
        set(value) {
            if (targetId > 0)
                KvManger.put(KvKey.VIEW_OTHER_LINK.plus(Account.userId).plus(targetId), value)
            field = value
        }
        get() {
            if (targetId > 0)
                field =
                    KvManger.getBoolean(KvKey.VIEW_OTHER_LINK.plus(Account.userId).plus(targetId))
            return field
        }
    internal var hasViewedVideo: Boolean = false
        set(value) {
            if (targetId > 0)
                KvManger.put(KvKey.VIEW_OTHER_VIDEO.plus(Account.userId).plus(targetId), value)
            field = value
        }
        get() {
            if (targetId > 0)
                field =
                    KvManger.getBoolean(KvKey.VIEW_OTHER_VIDEO.plus(Account.userId).plus(targetId))
            return field
        }
    internal var hasViewedImage: Boolean = false
        set(value) {
            if (targetId > 0)
                KvManger.put(KvKey.VIEW_OTHER_IMAGE.plus(Account.userId).plus(targetId), value)
            field = value
        }
        get() {
            if (targetId > 0)
                field =
                    KvManger.getBoolean(KvKey.VIEW_OTHER_IMAGE.plus(Account.userId).plus(targetId))
            return field
        }

    internal fun getUserTotalInfo(userId: Long) {
        ioScope {
            val apiResponse = if (userId > 0)
                UserInfoRepository.getUserInfoByNetwork(userId)
            else
                UserInfoRepository.getNextUserInfoByNetwork()
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                targetId = apiResult.targetId
                userInfoFlow.emit(apiResult)
            } else userInfoFlow.emit(null)
        }
    }

    internal fun changeFollowedStatus() {
        ioScope {
            val targetId = userInfoFlow.value?.targetId ?: 0
            val isFollowed = !(userInfoFlow.value?.targetIsFollowed ?: false)
            val apiResponse =
                UserInfoRepository.changeFollowedStatus(targetId, isFollowed)
            if (apiResponse.success) {
                userInfoFlow.value?.targetIsFollowed = isFollowed
                userFollowedStatusFlow.emit(isFollowed)
            } else userFollowedStatusFlow.emit(null)
        }
    }

    internal fun loadImageAd(
        activity: UserInfoActivity,
        failure: () -> Unit,
        success: () -> Unit
    ) {
        if (adIsLoading) return
        adIsLoading = true
        FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_5)
        val adUnitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_IMAGE)
        var interstitial: ATInterstitial? = null
        if (adUnitId.isNotBlank()) {
            interstitial = TopOnManager.loadInterstitial(
                activity = activity,
                adUnitId = adUnitId,
                loadFailureRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED_5, adUnitId, it)
                    failure()
                    adIsLoading = false
                },
                loadSuccessRequest = {
                    interstitial?.show(activity)
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_5)
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_5, adUnitId, it)
                    failure()
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_5)
                    success()
                    adIsLoading = false
                    hasViewedImage = true
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_5)
                })
        } else failure()
    }
}