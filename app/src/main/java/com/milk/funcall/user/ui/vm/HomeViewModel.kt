package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.milk.funcall.common.data.ApiPagingResponse
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.repo.HomeRepository

class HomeViewModel : ViewModel() {
    private val homeRepository by lazy { HomeRepository() }
    private var groupNumber: Int = 0

    val pagingSource = Pager(
        PagingConfig(
            pageSize = 12,
            prefetchDistance = 1,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NetworkPagingSource { getHomeList(it) }
        }
    )

    private suspend fun getHomeList(index: Int): ApiPagingResponse<UserInfoModel> {
        val apiResponse = homeRepository.getHomeList(index, groupNumber)
        val apiResult = apiResponse.data?.records
        if (apiResponse.success) groupNumber = apiResponse.data?.groupNumber ?: 0
        if (index == 1) apiResult?.forEachIndexed { position, homDetailModel ->
            if (position == 1) homDetailModel.isMediumImage = true
        }
        return ApiPagingResponse(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResult
        )
    }
}