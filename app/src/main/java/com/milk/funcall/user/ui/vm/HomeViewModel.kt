package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.funcall.common.data.ApiPagingResponse
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.user.data.UserSimpleInfoModel
import com.milk.funcall.user.repo.HomeRepository

class HomeViewModel : ViewModel() {
    private val homeRepository by lazy { HomeRepository() }
    private var groupNumber: Int = 0
    private var hasAddNativeAd = false
    var nativeAd: NativeAd? = null

    val pagingSource = Pager(
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
        if (apiResponse.success) groupNumber = apiResponse.data?.groupNumber ?: 0
        if (index == 1) apiResult?.forEachIndexed { position, homDetailModel ->
            if (position == 1) homDetailModel.isMediumImage = true
        }
        if (nativeAd != null && !hasAddNativeAd) {
            val userSimpleInfoModel = UserSimpleInfoModel(nativeAd = nativeAd)
            if (apiResult?.size ?: 0 > 4)
                apiResult?.add(3, userSimpleInfoModel)
            else
                apiResult?.add(userSimpleInfoModel)
            hasAddNativeAd = true
        }
        return ApiPagingResponse(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResult
        )
    }
}