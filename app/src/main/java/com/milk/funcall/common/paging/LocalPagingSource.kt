package com.milk.funcall.common.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource

class LocalPagingSource<Key : Any, Value : Any>(
    private val pageSize: Int = 20,
    private val prefetchDistance: Int = 1,
    private val pagingSourceFactory: () -> PagingSource<Key, Value>
) {
    internal val pager: Pager<Key, Value>
        get() = Pager(
            PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        )
}
