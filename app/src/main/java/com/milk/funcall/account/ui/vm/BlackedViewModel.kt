package com.milk.funcall.account.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.milk.funcall.account.data.BlackedModel
import com.milk.funcall.account.repo.BlackedRepository
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.common.response.ApiPagingResponse

class BlackedViewModel : ViewModel() {
    private val blackedRepository by lazy { BlackedRepository() }
    val pagingSource = Pager(
        config =  PagingConfig(
            pageSize = 20,
            prefetchDistance = 4,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NetworkPagingSource { getBlackedList(it) }
        }
    )

    private suspend fun getBlackedList(index: Int): ApiPagingResponse<BlackedModel> {
        val apiResponse = blackedRepository.getBlackedList(index)
        val apiResult = apiResponse.data?.records
        return ApiPagingResponse(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResult
        )
    }
}