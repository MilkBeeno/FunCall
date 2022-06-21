package com.milk.funcall.common.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.milk.funcall.R

abstract class AbstractPageAdapter<T : Any>(
    private val layoutId: Int,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    private val footLoadStateAdapter by lazy {
        object : AbstractLoadStateAdapter(
            footLayoutId = R.layout.view_paging_foot,
            onBindTailView = ::onBindTailView
        ) {

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindItemView(holder.itemView, getItem(position), position)
    }

    fun withLoadStateAdapter(): ConcatAdapter = withLoadStateFooter(footLoadStateAdapter)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(layoutId, parent, false))

    }

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v)

    abstract fun onBindItemView(v: View, item: T?, position: Int)

    abstract fun onBindTailView(v: View, state: AbstractLoadStateAdapter.LoadMoreState)
}