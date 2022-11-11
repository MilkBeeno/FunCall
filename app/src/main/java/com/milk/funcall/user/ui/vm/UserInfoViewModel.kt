package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.anythink.interstitial.api.ATInterstitial
import com.milk.funcall.account.Account
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.repo.ReportRepository
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.funcall.user.status.ReportType
import com.milk.funcall.user.status.UnlockType
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class UserInfoViewModel : ViewModel() {
    private var userInfoModel: UserInfoModel? = null
    internal val loadUserInfoStatusFlow = MutableSharedFlow<Boolean>()
    internal val changeFollowedStatusFlow = MutableSharedFlow<Boolean>()
    internal val changeUnlockStatusFlow = MutableSharedFlow<Boolean>()
    internal val reportFlow = MutableSharedFlow<Boolean>()

    internal fun loadUserInfo(userId: Long, deviceId: String) {
        ioScope {
            val userInfoResponse = if (userId > 0) {
                UserInfoRepository.getUserInfoByNetwork(userId)
            } else {
                UserInfoRepository.getNextUserInfoByNetwork()
            }
            val userInfoResult = userInfoResponse.data
            // 已经订阅的状态下、直接不请求免费解锁接口、直接免广告
            if (Account.userSubscribe) {
                userInfoModel = userInfoResult
                loadUserInfoStatusFlow.emit(userInfoResponse.success && userInfoResult != null)
            } else {
                // 未订阅状态下、需要根据设备号查看是否需要展示解锁接口
                val userUnlockResponse = UserInfoRepository.getUnlockInfo(deviceId, userId)
                userUnlockResponse.success = true
                val userUnlockResult = userUnlockResponse.data
                if (userInfoResult != null && userUnlockResult != null) {
                    userInfoResult.unlockMethod = userUnlockResult.unlockMethod
                    userInfoResult.remainUnlockCount = userUnlockResult.remainUnlockCount
                    // 解锁内容(1：联系方式，2：图片；3：视频)
                    userInfoResult.linkUnlocked = userUnlockResult.unlockMedias.contains(1)
                    userInfoResult.videoUnlocked = userUnlockResult.unlockMedias.contains(3)
                    userInfoResult.imageUnlocked = userUnlockResult.unlockMedias.contains(2)
                    userInfoModel = userInfoResult
                }
                loadUserInfoStatusFlow.emit(
                    userInfoResponse.success && userInfoResult != null
                        && userUnlockResponse.success && userUnlockResult != null
                )
            }
        }
    }

    internal fun changeUnlockStatus(deviceId: String, unlockType: Int, unlockUserId: Long) {
        ioScope {
            val apiResponse =
                UserInfoRepository.changeUnlockStatus(deviceId, unlockType, unlockUserId)
            // 解锁成功信息同步到服务器中、修改本地剩余次数数据并更新 UI
            if (apiResponse.success) {
                val userInfo = getUserInfoModel()
                val count = userInfo.remainUnlockCount - 1
                userInfo.remainUnlockCount = if (count <= 0) 0 else count
                when (unlockType) {
                    UnlockType.Link.value ->
                        userInfo.linkUnlocked = true
                    UnlockType.Image.value ->
                        userInfo.imageUnlocked = true
                }
            }
            changeUnlockStatusFlow.emit(apiResponse.success)
        }
    }

    internal fun getUserInfoModel() = checkNotNull(userInfoModel)

    internal fun changeFollowedStatus() {
        ioScope {
            val followedUserInfoModel = getUserInfoModel()
            val targetId = followedUserInfoModel.targetId
            val isFollowed = !followedUserInfoModel.targetIsFollowed
            val apiResponse = UserInfoRepository.changeFollowedStatus(targetId, isFollowed)
            if (apiResponse.success) {
                followedUserInfoModel.targetIsFollowed = isFollowed
                changeFollowedStatusFlow.emit(true)
            } else changeFollowedStatusFlow.emit(false)
        }
    }

    /** 查看联系人激励视频广告加载 */
    internal fun loadLinkAd(activity: UserInfoActivity, failure: () -> Unit, success: () -> Unit) {
        val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_LINK)
        if (adUnitId.isNotBlank()) {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_6)
            AdManager.loadIncentiveVideoAd(
                activity = activity,
                adUnitId = adUnitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_6, adUnitId, it)
                    failure()
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_6)
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_6, adUnitId, it)
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_6)
                    success()
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_6)
                }
            )
        }
    }

    /** 查看个人照片插页广告加载 */
    internal fun loadImageAd(activity: UserInfoActivity, failure: () -> Unit, success: () -> Unit) {
        FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_5)
        val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_IMAGE)
        var interstitial: ATInterstitial? = null
        if (adUnitId.isNotBlank()) {
            interstitial = AdManager.loadInterstitial(
                activity = activity,
                adUnitId = adUnitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_5, adUnitId, it)
                    failure()
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
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_5)
                })
        } else failure()
    }

    internal fun report(userId: Long, type: ReportType) {
        ioScope {
            val response = ReportRepository.report(userId, type)
            reportFlow.emit(response.success)
        }
    }
}