package com.milk.funcall.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineScope

class DBPagingSource<Key : Any, Value : Any>(
    val viewModelScope: CoroutineScope,
    pageSize: Int = 20,
    prefetchDistance: Int = 5,
    pagingSourceFactory: () -> PagingSource<Key, Value>
) {
    val pager = Pager(
        PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = false
        ),
        pagingSourceFactory = pagingSourceFactory
    )
}