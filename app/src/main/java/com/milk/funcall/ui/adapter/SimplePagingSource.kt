package com.milk.funcall.ui.adapter

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class SimplePagingSource<Key : Any, Value : Any>(
    val viewModelScope: CoroutineScope,
    pageSize: Int = 20,
    prefetchDistance: Int = 5,
    pagingSourceFactory: () -> PagingSource<Key, Value>
) {
    val pager: Pager<Key, Value> = Pager(
        PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = false
        ),
        pagingSourceFactory = pagingSourceFactory
    )

    fun flow(): Flow<PagingData<Value>> = pager.flow
}
