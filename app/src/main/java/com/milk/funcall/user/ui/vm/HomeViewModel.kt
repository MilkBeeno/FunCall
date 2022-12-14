package com.milk.funcall.user.ui.vm

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.milk.funcall.R
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.common.response.ApiPagingResponse
import com.milk.funcall.user.data.SayHiModel
import com.milk.funcall.user.data.UserSimpleInfoModel
import com.milk.funcall.user.repo.HomeRepository
import com.milk.funcall.user.status.ItemAdType
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.mainScope
import com.milk.simple.ktx.showToast
import com.milk.simple.ktx.string
import kotlinx.coroutines.flow.MutableSharedFlow

class HomeViewModel : ViewModel() {
    private val homeRepository by lazy { HomeRepository() }
    private var groupNumber: Int = 0
    private var nextPositionSpace = 3
    private var lastAddItemAdType: ItemAdType = ItemAdType.Null
    private val homeListFirst: Boolean
        get() = AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_FIRST)
            .isNotBlank() && AdConfig.adCancelType != 2
    private val homeListSecond: Boolean
        get() = AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_SECOND)
            .isNotBlank() && AdConfig.adCancelType != 2
    private val homeListThird: Boolean
        get() = AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_THIRD)
            .isNotBlank() && AdConfig.adCancelType != 2
    internal val sayHiFlow = MutableSharedFlow<MutableList<SayHiModel>>()
    internal val pagingSource = Pager(
        config = PagingConfig(
            pageSize = 8,
            prefetchDistance = 2,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NetworkPagingSource { getHomeList(it) }
        }
    )
    private var rewardVideoAd: ATRewardVideoAd? = null

    /** ????????????????????????????????? */
    internal fun loadSayHiAd(activity: FragmentActivity) {
        val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.SAY_HI_USER_AD)
        if (adUnitId.isNotBlank()) {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_12)
            rewardVideoAd = AdManager.getIncentiveVideoAd(
                activity = activity,
                adUnitId = adUnitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_12, it)
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_12)
                    ioScope {
                        val apiResponse = homeRepository.getSayHiList()
                        val apiResult = apiResponse.data
                        if (apiResponse.success && apiResult != null) {
                            sayHiFlow.emit(apiResult)
                        }
                    }
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_12, it)
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_12)
                    mainScope { activity.showToast(activity.string(R.string.common_success)) }
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_12)
                }
            )
        }
    }

    internal fun showSayHiAd(activity: FragmentActivity) {
        rewardVideoAd?.show(activity)
    }

    internal fun sendTextMessage(context: Context, sayHiModels: MutableList<SayHiModel>) {
        ioScope {
            sayHiModels.forEach { sayHiModel ->
                MessageRepository.sendTextChatMessage(
                    sayHiModel.userId,
                    sayHiModel.userName,
                    sayHiModel.userAvatar,
                    context.string(R.string.chat_message_say_hi_title)
                )
            }
        }
    }

    private suspend fun getHomeList(index: Int): ApiPagingResponse<UserSimpleInfoModel> {
        val apiResponse = homeRepository.getHomeList(index, groupNumber)
        val apiResult = apiResponse.data?.records
        if (apiResponse.success && apiResult != null && apiResult.isNotEmpty()) {
            // ??????????????????????????????
            groupNumber = apiResponse.data.groupNumber
            // ??????????????????????????? nextPositionSpace ????????????
            if (index == 1) {
                nextPositionSpace = 3
                apiResult.forEachIndexed { position, homDetailModel ->
                    if (position == 1) homDetailModel.isMediumImage = true
                }
            }
            // ??????????????????
            addAdToHomeList(
                four = { space ->
                    // nextPositionSpace ???????????? 3 ??????????????????????????????
                    if (apiResult.size >= nextPositionSpace) {
                        lastAddItemAdType = when (lastAddItemAdType) {
                            ItemAdType.FirstAd -> ItemAdType.SecondAd
                            ItemAdType.SecondAd -> ItemAdType.ThirdAd
                            else -> ItemAdType.FirstAd
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(itemAdType = lastAddItemAdType)
                        apiResult.add(nextPositionSpace, userSimpleInfoModel)
                    }
                    if (apiResult.size >= nextPositionSpace + space) {
                        lastAddItemAdType = when (lastAddItemAdType) {
                            ItemAdType.FirstAd -> ItemAdType.SecondAd
                            ItemAdType.SecondAd -> ItemAdType.ThirdAd
                            else -> ItemAdType.FirstAd
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(itemAdType = lastAddItemAdType)
                        apiResult.add(nextPositionSpace + space, userSimpleInfoModel)
                    }
                    if (apiResult.size >= nextPositionSpace + 2 * space) {
                        lastAddItemAdType = when (lastAddItemAdType) {
                            ItemAdType.FirstAd -> ItemAdType.SecondAd
                            ItemAdType.SecondAd -> ItemAdType.ThirdAd
                            else -> ItemAdType.FirstAd
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(itemAdType = lastAddItemAdType)
                        apiResult.add(nextPositionSpace + 2 * space, userSimpleInfoModel)
                        nextPositionSpace += 3 * space - apiResult.size
                    } else nextPositionSpace += 2 * space - apiResult.size
                },
                eight = { space, pair ->
                    if (apiResult.size > nextPositionSpace) {
                        lastAddItemAdType = when (lastAddItemAdType) {
                            pair.first -> pair.second
                            else -> pair.first
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(itemAdType = lastAddItemAdType)
                        apiResult.add(nextPositionSpace, userSimpleInfoModel)
                    }
                    if (apiResult.size > nextPositionSpace + space) {
                        lastAddItemAdType = when (lastAddItemAdType) {
                            pair.first -> pair.second
                            else -> pair.first
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(itemAdType = lastAddItemAdType)
                        apiResult.add(nextPositionSpace + space, userSimpleInfoModel)
                        nextPositionSpace += 2 * space - apiResult.size
                    } else nextPositionSpace += space - apiResult.size
                },
                twelve = { space, item ->
                    if (apiResult.size > nextPositionSpace) {
                        val userSimpleInfoModel = UserSimpleInfoModel(itemAdType = item)
                        apiResult.add(nextPositionSpace, userSimpleInfoModel)
                        nextPositionSpace += space - apiResult.size
                    } else nextPositionSpace %= 8
                })
        }
        return ApiPagingResponse(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResult
        )
    }

    /** 1.??????????????????????????? 12 ????????? 2.??????????????????????????? 8 ????????? 3.????????????????????? 4 ????????? */
    private fun addAdToHomeList(
        four: (Int) -> Unit,
        eight: (Int, Pair<ItemAdType, ItemAdType>) -> Unit,
        twelve: (Int, ItemAdType) -> Unit
    ) {
        when {
            homeListFirst && homeListSecond && homeListThird ->
                four(4)
            homeListFirst && homeListSecond && !homeListThird ->
                eight(8, Pair(ItemAdType.FirstAd, ItemAdType.SecondAd))
            homeListFirst && homeListThird && !homeListSecond ->
                eight(8, Pair(ItemAdType.FirstAd, ItemAdType.ThirdAd))
            homeListSecond && homeListThird && !homeListFirst ->
                eight(8, Pair(ItemAdType.SecondAd, ItemAdType.ThirdAd))
            homeListFirst && !homeListSecond && !homeListThird ->
                twelve(12, ItemAdType.FirstAd)
            homeListSecond && !homeListFirst && !homeListThird ->
                twelve(12, ItemAdType.SecondAd)
            homeListThird && !homeListFirst && !homeListSecond ->
                twelve(12, ItemAdType.ThirdAd)
            else -> Unit
        }
    }
}