package com.milk.funcall.common.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.milk.funcall.common.response.ApiPagingResponse

class NetworkPagingSource<T : Any>(
    private val netWorkRequest: suspend (Int) -> ApiPagingResponse<T>
) : PagingSource<Int, T>() {
    // 实现必须定义如何从已加载分页数据的中间恢复刷新,使用 state.anchorPosition 作为最近访问的索引来映射正确的初始键.
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val pageIndex = params.key ?: 1
            val apiPagingResponse = netWorkRequest(pageIndex)
            val result = apiPagingResponse.data
            val prevKey = if (pageIndex > 1) pageIndex - 1 else null
            val nextKey = if (result != null && result.size > 0) pageIndex + 1 else null
            if (result != null) LoadResult.Page(
                data = result,
                prevKey = prevKey,
                nextKey = nextKey
            ) else LoadResult.Error(Throwable(" The data is NULL "))
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}