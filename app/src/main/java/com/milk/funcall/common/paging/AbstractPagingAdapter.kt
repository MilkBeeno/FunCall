package com.milk.funcall.common.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.common.paging.status.AppendStatus
import com.milk.funcall.common.paging.status.RefreshStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class AbstractPagingAdapter<T : Any>(
    private val layoutId: Int? = null,
    private val coroutineScope: CoroutineScope? = null,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, PagingViewHolder>(diffCallback), PagingAdapter<T> {

    protected var pairHeaderAndFooter = Pair(first = false, second = false)
    private var multiTypeDelegateImpl: MultiTypeDelegate? = null
    private val childClickViewIds = LinkedHashSet<Int>()
    private val childLongClickViewIds = LinkedHashSet<Int>()
    private var itemClickListener: (
        (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit)? = null
    private var itemLongClickListener: (
        (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean)? = null
    private var itemChildClickListener: (
        (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit)? = null
    private var itemChildLongClickListener: (
        (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean)? = null

    private var refreshing = false
    private var appending = false
    private var refreshedListener: ((RefreshStatus) -> Unit)? = null
    private var appendedListener: ((AppendStatus) -> Unit)? = null

    init {
        addLoadStateListener { combinedLoadStates ->
            when (combinedLoadStates.source.refresh) {
                is LoadState.Loading -> refreshing = true
                is LoadState.NotLoading -> {
                    if (refreshing) {
                        refreshing = false
                        if (itemCount <= 0)
                            refreshedListener?.invoke(RefreshStatus.Empty)
                        else
                            refreshedListener?.invoke(RefreshStatus.Success)
                    }
                }
                else -> refreshedListener?.invoke(
                    if (itemCount < 0) RefreshStatus.Error else RefreshStatus.Failed
                )
            }
            when (combinedLoadStates.source.append) {
                is LoadState.Loading -> appending = true
                is LoadState.NotLoading -> {
                    if (appending) {
                        appending = false
                        appendedListener?.invoke(AppendStatus.Success)
                    }
                }
                else -> appendedListener?.invoke(AppendStatus.Failed)
            }
        }
    }

    override fun createHeaderAdapter(): HeaderLoadStateAdapter? = null

    override fun createFooterAdapter(): FooterLoadStateAdapter? = null

    override fun withLoadStateHeaderAdapter(): ConcatAdapter {
        val headerAdapter = checkNotNull(createHeaderAdapter())
        pairHeaderAndFooter = Pair(first = true, second = false)
        return withLoadStateHeader(headerAdapter)
    }

    override fun withLoadStateFooterAdapter(): ConcatAdapter {
        val footerAdapter = checkNotNull(createFooterAdapter())
        pairHeaderAndFooter = Pair(first = false, second = true)
        return withLoadStateFooter(footerAdapter)
    }

    override fun withLoadStateHeaderAndFooterAdapter(): ConcatAdapter {
        val headerAdapter = checkNotNull(createHeaderAdapter())
        val footerAdapter = checkNotNull(createFooterAdapter())
        return withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
    }

    override fun setMultiTypeDelegate(multiTypeDelegate: MultiTypeDelegate?) {
        multiTypeDelegateImpl = multiTypeDelegate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val finalLayoutId = multiTypeDelegateImpl?.getItemViewId(viewType) ?: layoutId
        checkNotNull(finalLayoutId)
        return PagingViewHolder(
            LayoutInflater.from(parent.context).inflate(finalLayoutId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        dispatchClickEvent(holder)
        convert(holder, getItem(position)!!)
    }

    private fun dispatchClickEvent(holder: PagingViewHolder) {
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener {
                if (holder.bindingAdapterPosition in 0 until itemCount) {
                    itemClickListener?.invoke(this, it, holder.bindingAdapterPosition)
                }
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
        if (itemLongClickListener != null) {
            holder.itemView.setOnLongClickListener {
                if (holder.bindingAdapterPosition in 0 until itemCount) {
                    val invoke = itemLongClickListener
                        ?.invoke(this, it, holder.bindingAdapterPosition)
                    return@setOnLongClickListener invoke ?: false
                }
                return@setOnLongClickListener false
            }
        } else {
            holder.itemView.setOnLongClickListener(null)
        }
        childClickViewIds.forEach { id ->
            val view = holder.getViewOrNull<View>(id)
            if (itemChildClickListener != null) {
                view?.setOnClickListener {
                    if (holder.bindingAdapterPosition in 0 until itemCount) {
                        itemChildClickListener
                            ?.invoke(this, it, holder.bindingAdapterPosition)
                    }
                }
            } else {
                view?.setOnClickListener(null)
            }
        }
        childLongClickViewIds.forEach { id ->
            val view = holder.getViewOrNull<View>(id)
            if (itemChildLongClickListener != null) {
                view?.setOnLongClickListener {
                    if (holder.bindingAdapterPosition in 0 until itemCount) {
                        val invoke = itemChildLongClickListener?.invoke(
                            this,
                            it,
                            holder.bindingAdapterPosition
                        )
                        return@setOnLongClickListener invoke ?: false
                    }
                    return@setOnLongClickListener false
                }
            } else {
                view?.setOnLongClickListener(null)
            }
        }
    }

    override fun <K : Any> setPagerSource(pager: Pager<K, T>) {
        coroutineScope ?: MainScope().launch(Dispatchers.IO) {
            pager.flow.collectLatest {
                submitData(it)
            }
        }
    }

    override fun setOnItemClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit
    ) {
        itemClickListener = listener
    }

    override fun setOnItemLongClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean
    ) {
        itemLongClickListener = listener
    }

    override fun setOnItemChildClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit
    ) {
        itemChildClickListener = listener
    }

    override fun setOnItemChildLongClickListener(
        listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean
    ) {
        itemChildLongClickListener = listener
    }

    override fun addChildClickViewIds(vararg viewIds: Int) {
        viewIds.forEach { childClickViewIds.add(it) }
    }

    override fun addChildLongClickViewIds(vararg viewIds: Int) {
        viewIds.forEach { childLongClickViewIds.add(it) }
    }

    override fun addRefreshedListener(listener: (RefreshStatus) -> Unit) {
        refreshedListener = listener
    }

    override fun addAppendedListener(listener: (AppendStatus) -> Unit) {
        appendedListener = listener
    }

    override fun removeRefreshedListener() {
        refreshedListener = null
    }

    override fun removeAppendedListener() {
        appendedListener = null
    }

    override fun getNoNullItem(position: Int): T {
        return checkNotNull(getItem(position))
    }
}