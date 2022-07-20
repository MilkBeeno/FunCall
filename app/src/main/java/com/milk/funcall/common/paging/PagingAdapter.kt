package com.milk.funcall.common.paging

import android.view.View
import androidx.annotation.IdRes
import androidx.paging.Pager
import androidx.recyclerview.widget.ConcatAdapter
import com.milk.funcall.common.paging.status.AppendStatus
import com.milk.funcall.common.paging.status.RefreshStatus

interface PagingAdapter<T : Any> {

    fun withLoadStateHeaderAdapter(): ConcatAdapter
    fun withLoadStateFooterAdapter(): ConcatAdapter
    fun withLoadStateHeaderAndFooterAdapter(): ConcatAdapter
    fun setMultiTypeDelegate(multiTypeDelegate: MultiTypeDelegate?)
    fun convert(holder: PagingViewHolder, item: T)
    fun addRefreshedListener(listener: (RefreshStatus) -> Unit)
    fun addAppendedListener(listener: (AppendStatus) -> Unit)
    fun removeRefreshedListener()
    fun removeAppendedListener()
    fun <K : Any> setPagerSource(pager: Pager<K, T>)
    fun addChildClickViewIds(@IdRes vararg viewIds: Int)
    fun addChildLongClickViewIds(@IdRes vararg viewIds: Int)
    fun getNoNullItem(position: Int): T

    fun setOnItemClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit
    )

    fun setOnItemChildClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit
    )

    fun setOnItemLongClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean
    )

    fun setOnItemChildLongClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean
    )

    fun createHeaderAdapter(): HeaderLoadStateAdapter?
    fun createFooterAdapter(): FooterLoadStateAdapter?
}