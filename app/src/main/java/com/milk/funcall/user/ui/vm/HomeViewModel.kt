package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.funcall.common.data.ApiPagingResponse
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.user.data.UserSimpleInfoModel
import com.milk.funcall.user.repo.HomeRepository
import com.milk.funcall.user.type.AdType
import com.milk.simple.log.Logger

class HomeViewModel : ViewModel() {
    private val homeRepository by lazy { HomeRepository() }
    private var groupNumber: Int = 0
    private var nextPositionSpace = 3

    // 只有两个广告时保存广告信息
    private var homePageAds = Pair<NativeAd?, NativeAd?>(null, null)
    private var lastAddNativeAd: NativeAd? = null
    internal var firstHomePageAd: NativeAd? = null
    internal var secondHomePageAd: NativeAd? = null
    internal var thirdHomePageAd: NativeAd? = null

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
                    Logger.d(
                        "firstHomePageAd=$firstHomePageAd," +
                                "secondHomePageAd=$secondHomePageAd," +
                                "thirdHomePageAd=$thirdHomePageAd",
                        "HomeViewModel"
                    )
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
                        Logger.d("当前AD是=$lastAddNativeAd", "HomeViewModel")
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
                        Logger.d("当前AD是=$lastAddNativeAd", "HomeViewModel")
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
                        Logger.d("当前AD是=$lastAddNativeAd", "HomeViewModel")
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
}