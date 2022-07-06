package com.milk.funcall.account.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.milk.funcall.account.repo.FansOrFollowsRepository
import com.milk.funcall.common.data.ApiPagingResponse
import com.milk.funcall.common.mdr.table.UserInfoEntity
import com.milk.funcall.common.paging.NetworkPagingSource

class FansViewModel : ViewModel() {
    private val fansOrFollowsRepository by lazy { FansOrFollowsRepository() }
    val pagingSource = Pager(
        PagingConfig(
            pageSize = 8,
            prefetchDistance = 2,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NetworkPagingSource { getFans(it) }
        }
    )

    private suspend fun getFans(index: Int): ApiPagingResponse<UserInfoEntity> {
        val apiResponse = fansOrFollowsRepository.getFans(index)
        val apiResult = apiResponse.data?.records
        return ApiPagingResponse(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResult
        )
    }
}