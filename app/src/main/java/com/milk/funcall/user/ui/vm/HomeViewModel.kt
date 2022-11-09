package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.common.response.ApiPagingResponse
import com.milk.funcall.user.data.UserSimpleInfoModel
import com.milk.funcall.user.repo.HomeRepository
import com.milk.funcall.user.status.ItemAdType

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
            addAdToHomeList(
                four = { space ->
                    // nextPositionSpace 默认值是 3 表示列表的第四个位置
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

    /** 1.只有一个广告时相隔 12 个位置 2.只有两个广告时相隔 8 个位置 3.三个广告时想隔 4 个位置 */
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