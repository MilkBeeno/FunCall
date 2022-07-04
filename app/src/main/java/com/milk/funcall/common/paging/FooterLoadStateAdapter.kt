package com.milk.funcall.common.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView

open class FooterLoadStateAdapter(
    @LayoutRes private val footLayoutId: Int = 0,
    private val pageSize: Int = 0,
    private val hasHeader: Boolean = false,
    private val bindFooterView: (v: View, state: LoadMoreState) -> Unit? = { _, _ -> }
) : LoadStateAdapter<FooterLoadStateAdapter.FooterViewHolder>() {
    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                bindFooterView(holder.itemView, LoadMoreState.Loading)
            }
            is LoadState.Error -> {
                bindFooterView(holder.itemView, LoadMoreState.Error)
            }
            is LoadState.NotLoading -> {
                val onePageSize = pageSize + if (hasHeader) 1 else 0
                if (loadState.endOfPaginationReached
                    && holder.absoluteAdapterPosition >= onePageSize
                ) {
                    bindFooterView(holder.itemView, LoadMoreState.NoMoreData)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FooterViewHolder {
        if (footLayoutId <= 0) return FooterViewHolder(View(parent.context))
        val footView =
            LayoutInflater.from(parent.context).inflate(footLayoutId, parent, false)
        return FooterViewHolder(footView)
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return !(loadState is LoadState.NotLoading && !loadState.endOfPaginationReached)
    }

    inner class FooterViewHolder(footView: View) : RecyclerView.ViewHolder(footView)
    enum class LoadMoreState { Loading, NoMoreData, Error }
}