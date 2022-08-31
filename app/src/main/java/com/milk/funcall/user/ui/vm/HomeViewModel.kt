package com.milk.funcall.user.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdManager
import com.milk.funcall.ad.AdSwitch
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.common.data.ApiPagingResponse
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.common.timer.MilkTimer
import com.milk.funcall.firebase.FireBaseManager
import com.milk.funcall.firebase.constant.FirebaseKey
import com.milk.funcall.user.data.UserSimpleInfoModel
import com.milk.funcall.user.repo.HomeRepository
import com.milk.funcall.user.type.AdType
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine

class HomeViewModel : ViewModel() {
    private val homeRepository by lazy { HomeRepository() }

    // 广告加载的状态
    private var loadFirstAd = MutableSharedFlow<Boolean>()
    private var loadSecondAd = MutableSharedFlow<Boolean>()
    private var loadThirdAd = MutableSharedFlow<Boolean>()
    internal val loadAdStatus =
        combine(loadFirstAd, loadSecondAd, loadThirdAd) { a, b, c -> arrayOf(a, b, c) }
    private var groupNumber: Int = 0
    private var nextPositionSpace = 3

    // 只有两个广告时保存广告信息
    private var homePageAds = Pair<NativeAd?, NativeAd?>(null, null)
    private var lastAddNativeAd: NativeAd? = null

    private var firstHomePageAd: NativeAd? = null
    private var secondHomePageAd: NativeAd? = null
    private var thirdHomePageAd: NativeAd? = null

    internal val pagingSource = Pager(
        PagingConfig(
            pageSize = 8,
            prefetchDistance = 2,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NetworkPagingSource { getHomeList(it) }
        }
    )

    internal fun loadNativeAd(context: Context) {
        getFirstHomePageAd(context, true)
        getSecondHomePageAd(context, true)
        getThirdHomePageAd(context, true)
    }

    internal fun loadNativeAdByTimer(context: Context) {
        MilkTimer.Builder()
            .setMillisInFuture(30000)
            .setCountDownInterval(1000)
            .setOnFinishedListener {
                getFirstHomePageAd(context, false)
                getSecondHomePageAd(context, false)
                getThirdHomePageAd(context, false)
                loadNativeAdByTimer(context)
            }.build().start()
    }

    private suspend fun getHomeList(index: Int): ApiPagingResponse<UserSimpleInfoModel> {
        val apiResponse = homeRepository.getHomeList(index, groupNumber)
        val apiResult = apiResponse.data?.records
        if (apiResponse.success && apiResult != null && apiResult.isNotEmpty()) {
            // 更新上拉刷新分组标志
            groupNumber = apiResponse.data.groupNumber
            // 下拉刷新状态、更新 nextPositionSpace 为初始值
            if (index == 1) {
                nextPositionSpace = 3
                apiResult.forEachIndexed { position, homDetailModel ->
                    if (position == 1) homDetailModel.isMediumImage = true
                }
            }
            // 广告插入处理
            homeAdSpaceCount(
                homeType = homeAdType(),
                four = { space ->
                    // nextPositionSpace 默认值是 3 表示列表的第四个位置
                    if (apiResult.size >= nextPositionSpace) {
                        lastAddNativeAd = when (lastAddNativeAd) {
                            firstHomePageAd -> secondHomePageAd
                            secondHomePageAd -> thirdHomePageAd
                            else -> firstHomePageAd
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(nativeAd = lastAddNativeAd)
                        apiResult.add(nextPositionSpace, userSimpleInfoModel)
                    }
                    if (apiResult.size >= nextPositionSpace + space) {
                        lastAddNativeAd = when (lastAddNativeAd) {
                            firstHomePageAd -> secondHomePageAd
                            secondHomePageAd -> thirdHomePageAd
                            else -> firstHomePageAd
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(nativeAd = lastAddNativeAd)
                        apiResult.add(nextPositionSpace + space, userSimpleInfoModel)
                    }
                    if (apiResult.size >= nextPositionSpace + 2 * space) {
                        lastAddNativeAd = when (lastAddNativeAd) {
                            firstHomePageAd -> secondHomePageAd
                            secondHomePageAd -> thirdHomePageAd
                            else -> firstHomePageAd
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(nativeAd = lastAddNativeAd)
                        apiResult.add(nextPositionSpace + 2 * space, userSimpleInfoModel)
                        nextPositionSpace += 3 * space - apiResult.size
                    } else nextPositionSpace += 2 * space - apiResult.size
                },
                eight = { space ->
                    if (apiResult.size > nextPositionSpace) {
                        lastAddNativeAd = when (lastAddNativeAd) {
                            homePageAds.first -> homePageAds.second
                            else -> homePageAds.first
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(nativeAd = lastAddNativeAd)
                        apiResult.add(nextPositionSpace, userSimpleInfoModel)
                    }
                    if (apiResult.size > nextPositionSpace + space) {
                        lastAddNativeAd = when (lastAddNativeAd) {
                            homePageAds.first -> homePageAds.second
                            else -> homePageAds.first
                        }
                        val userSimpleInfoModel =
                            UserSimpleInfoModel(nativeAd = lastAddNativeAd)
                        apiResult.add(nextPositionSpace + space, userSimpleInfoModel)
                        nextPositionSpace += 2 * space - apiResult.size
                    } else nextPositionSpace += space - apiResult.size
                },
                twelve = { space ->
                    if (apiResult.size > nextPositionSpace) {
                        val nativeAd = when {
                            firstHomePageAd != null -> firstHomePageAd
                            secondHomePageAd != null -> secondHomePageAd
                            else -> thirdHomePageAd
                        }
                        val userSimpleInfoModel = UserSimpleInfoModel(nativeAd = nativeAd)
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

    /** 广告间隔位置的数量 */
    private fun homeAdSpaceCount(
        homeType: AdType,
        four: (Int) -> Unit,
        eight: (Int) -> Unit,
        twelve: (Int) -> Unit
    ) {
        when (homeType) {
            AdType.All -> four(4)
            AdType.FirstAndSecond -> {
                homePageAds = Pair(firstHomePageAd, secondHomePageAd)
                eight(8)
            }
            AdType.FirstAndThird -> {
                homePageAds = Pair(firstHomePageAd, thirdHomePageAd)
                eight(8)
            }
            AdType.SecondAndThird -> {
                homePageAds = Pair(secondHomePageAd, thirdHomePageAd)
                eight(8)
            }
            AdType.First,
            AdType.Second,
            AdType.Third -> twelve(12)
            else -> Unit
        }
    }

    /** 按照需求定位：1.只有一个广告时相隔 12 个位置 2.只有两个广告时相隔 8 个位置 3.三个广告时想隔 4 个位置 */
    private fun homeAdType(): AdType {
        return when {
            firstHomePageAd != null
                    && secondHomePageAd != null
                    && thirdHomePageAd != null -> AdType.All
            firstHomePageAd != null
                    && secondHomePageAd != null
                    && thirdHomePageAd == null -> AdType.FirstAndSecond
            firstHomePageAd != null
                    && thirdHomePageAd != null
                    && secondHomePageAd == null -> AdType.FirstAndThird
            secondHomePageAd != null
                    && thirdHomePageAd != null
                    && firstHomePageAd == null -> AdType.SecondAndThird
            firstHomePageAd != null
                    && secondHomePageAd == null
                    && thirdHomePageAd == null -> AdType.First
            secondHomePageAd != null
                    && firstHomePageAd == null
                    && thirdHomePageAd == null -> AdType.Second
            thirdHomePageAd != null
                    && firstHomePageAd == null
                    && secondHomePageAd == null -> AdType.Third
            else -> AdType.Null
        }
    }

    private fun getFirstHomePageAd(context: Context, isFirstLoad: Boolean) {
        ioScope {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_1)
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_FIRST)
            if (adUnitId.isNotBlank() && AdSwitch.homeListFirst)
                AdManager.loadNativeAds(
                    context = context,
                    adUnitId = adUnitId,
                    loadFailedRequest = {
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_REQUEST_FAILED_1, adUnitId, it)
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_SHOW_FAILED_1, adUnitId, it)
                        firstHomePageAd = null
                        ioScope { loadFirstAd.emit(isFirstLoad) }
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_1)
                        firstHomePageAd = it
                        ioScope { loadFirstAd.emit(isFirstLoad) }
                    },
                    showSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_1)
                    },
                    clickRequest = {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_AD_1)
                    })
            else {
                delay(500)
                loadFirstAd.emit(isFirstLoad)
            }
        }
    }

    private fun getSecondHomePageAd(context: Context, isFirstLoad: Boolean) {
        ioScope {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_2)
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_SECOND)
            if (adUnitId.isNotBlank() && AdSwitch.homeListSecond)
                AdManager.loadNativeAds(
                    context = context,
                    adUnitId = adUnitId,
                    loadFailedRequest = {
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_REQUEST_FAILED_2, adUnitId, it)
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_SHOW_FAILED_2, adUnitId, it)
                        secondHomePageAd = null
                        ioScope { loadSecondAd.emit(isFirstLoad) }
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_2)
                        secondHomePageAd = it
                        ioScope { loadSecondAd.emit(isFirstLoad) }
                    },
                    showSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_2)
                    },
                    clickRequest = {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_AD_2)
                    })
            else {
                delay(500)
                loadSecondAd.emit(isFirstLoad)
            }
        }
    }

    private fun getThirdHomePageAd(context: Context, isFirstLoad: Boolean) {
        ioScope {
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_3)
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.HOME_LIST_THIRD)
            if (adUnitId.isNotBlank() && AdSwitch.homeListThird)
                AdManager.loadNativeAds(
                    context = context,
                    adUnitId = adUnitId,
                    loadFailedRequest = {
                        FireBaseManager
                            .logEvent(FirebaseKey.Ad_request_failed_3, adUnitId, it)
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_SHOW_FAILED_3, adUnitId, it)
                        thirdHomePageAd = null
                        ioScope { loadThirdAd.emit(isFirstLoad) }
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_3)
                        thirdHomePageAd = it
                        ioScope { loadThirdAd.emit(isFirstLoad) }
                    },
                    showSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_3)
                    },
                    clickRequest = {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_AD_3)
                    })
            else {
                delay(500)
                loadThirdAd.emit(isFirstLoad)
            }
        }
    }
}