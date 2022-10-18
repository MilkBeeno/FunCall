package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.anythink.interstitial.api.ATInterstitial
import com.milk.funcall.account.Account
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.TopOnManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import kotlinx.coroutines.flow.MutableSharedFlow

class UserInfoViewModel : ViewModel() {
    private var userInfoModel: UserInfoModel? = null
    internal val loadUserInfoStatusFlow = MutableSharedFlow<Boolean>()
    internal val changeFollowedStatusFlow = MutableSharedFlow<Boolean>()

    private val targetId by lazy { getUserInfoModel().targetId }
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

    internal fun loadUserInfo(userId: Long) {
        ioScope {
            val apiResponse = if (userId > 0) {
                UserInfoRepository.getUserInfoByNetwork(userId)
            } else {
                UserInfoRepository.getNextUserInfoByNetwork()
            }
            userInfoModel = apiResponse.data
            loadUserInfoStatusFlow.emit(apiResponse.success && userInfoModel != null)
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
            TopOnManager.loadIncentiveVideoAd(
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
            interstitial = TopOnManager.loadInterstitial(
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
                    hasViewedImage = true
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_5)
                })
        } else failure()
    }
}