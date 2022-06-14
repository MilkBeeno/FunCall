package com.milk.funcall.common.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class AbstractPagingAdapter<T : Any>(
    private val layoutId: Int? = null,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, PagingViewHolder>(diffCallback) {

    private var mMultiTypeDelegate: MultiTypeDelegate? = null
    private var refreshing = false
    private var appending = false
    private val refreshedListeners = mutableListOf<() -> Unit>()
    private val appendedListeners = mutableListOf<() -> Unit>()

    init {
        addLoadStateListener {
            when (it.source.refresh) {
                is LoadState.Loading -> refreshing = true
                is LoadState.NotLoading -> {
                    if (refreshing) {
                        refreshing = false
                        refreshedListeners.forEach { listener ->
                            listener.invoke()
                        }
                    }
                }
                else -> {
                }
            }
            when (it.source.append) {
                is LoadState.Loading -> appending = true
                is LoadState.NotLoading -> {
                    if (appending) {
                        appending = false
                        appendedListeners.forEach { listener ->
                            listener.invoke()
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    fun setMultiTypeDelegate(multiTypeDelegate: MultiTypeDelegate?) {
        this.mMultiTypeDelegate = multiTypeDelegate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val layout = mMultiTypeDelegate?.getItemViewId(viewType) ?: layoutId
        checkNotNull(layout)
        return PagingViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }


    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        setClick(holder, position)
        onConvert(holder, getItem(position)!!)
    }

    fun getData(position: Int): T {
        return getItem(position)!!
    }

    private fun setClick(holder: PagingViewHolder, position: Int) {
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
                    val invoke =
                        itemLongClickListener?.invoke(this, it, holder.bindingAdapterPosition)
                    return@setOnLongClickListener invoke ?: false
                }
                return@setOnLongClickListener false
            }
        } else {
            holder.itemView.setOnLongClickListener(null)
        }
        childClickViewIds.forEach {
            val view = holder.getViewOrNull<View>(it)
            if (itemChildClickListener != null) {
                view?.setOnClickListener {
                    if (holder.bindingAdapterPosition in 0 until itemCount) {
                        itemChildClickListener?.invoke(this, it, holder.bindingAdapterPosition)
                    }
                }
            } else {
                view?.setOnClickListener(null)
            }
        }
        childLongClickViewIds.forEach { it ->
            val view = holder.getViewOrNull<View>(it)
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

    abstract fun onConvert(holder: PagingViewHolder, item: T)

    fun addRefreshedListener(listener: () -> Unit) {
        refreshedListeners.add(listener)
    }

    fun addAppendedListener(listener: () -> Unit) {
        appendedListeners.add(listener)
    }

    fun removeRefreshedListener(listener: () -> Unit) {
        refreshedListeners.remove(listener)
    }

    fun removeAllRefreshedListener(listener: () -> Unit) {
        refreshedListeners.clear()
    }

    fun removeAppendedListener(listener: () -> Unit) {
        appendedListeners.remove(listener)
    }

    fun removeAllAppendedListener(listener: () -> Unit) {
        appendedListeners.clear()
    }

    fun <K : Any> setPagerSource(pagerSource: DBPagingSource<K, T>) {
        pagerSource.viewModelScope.launch(Dispatchers.IO) {
            pagerSource.pager.flow.collectLatest {
                submitData(it)
            }
        }
    }

    private var itemClickListener: ((adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit)? =
        null

    fun setOnItemClickListener(listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit) {
        itemClickListener = listener
    }

    private var itemChildClickListener: ((adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit)? =
        null

    fun setOnItemChildClickListener(listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Unit) {
        itemChildClickListener = listener
    }

    private var itemLongClickListener: ((adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean)? =
        null

    fun setOnItemLongClickListener(listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean) {
        itemLongClickListener = listener
    }

    private var itemChildLongClickListener: ((adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean)? =
        null

    fun setOnItemChildLongClickListener(listener: (adapter: AbstractPagingAdapter<T>, itemView: View, position: Int) -> Boolean) {
        itemChildLongClickListener = listener
    }

    private val childClickViewIds = LinkedHashSet<Int>()

    fun addChildClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childClickViewIds.add(viewId)
        }
    }

    private val childLongClickViewIds = LinkedHashSet<Int>()

    fun addChildLongClickViewIds(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            childLongClickViewIds.add(viewId)
        }
    }
}