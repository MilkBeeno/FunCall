package com.milk.funcall.account.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.milk.funcall.account.Account
import com.milk.funcall.account.repo.FansOrFollowsRepository
import com.milk.funcall.common.db.table.UserInfoEntity
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.common.response.ApiPagingResponse

class FansViewModel : ViewModel() {
    private val fansOrFollowsRepository by lazy { FansOrFollowsRepository() }
    val pagingSource = Pager(
        config = PagingConfig(
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
        val fansNumber = apiResponse.data?.total
        // 查看粉丝数量时更新粉丝数
        if (apiResponse.success && fansNumber != null) {
            Account.userFansFlow.emit(fansNumber)
            Account.userFans = fansNumber
        }
        return ApiPagingResponse(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResult
        )
    }
}