package com.milk.funcall.common.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractLoadStateAdapter(
    @LayoutRes private val footLayoutId: Int,
    private val onBindTailView: (v: View, state: LoadMoreState) -> Unit
) : LoadStateAdapter<AbstractLoadStateAdapter.FootViewHolder>() {
    override fun onBindViewHolder(holder: FootViewHolder, loadState: LoadState) {
        val rootView = holder.itemView
        when (loadState) {
            is LoadState.Loading -> onBindTailView(rootView, LoadMoreState.LOADING)
            is LoadState.Error -> onBindTailView(rootView, LoadMoreState.ERROR)
            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) onBindTailView(rootView, LoadMoreState.LOADED)
            }
            else -> {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FootViewHolder {
        val footView =
            LayoutInflater.from(parent.context).inflate(footLayoutId, parent, false)
        return FootViewHolder(footView)
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return !(loadState is LoadState.NotLoading && !loadState.endOfPaginationReached)
    }

    inner class FootViewHolder(footView: View) : RecyclerView.ViewHolder(footView)
    enum class LoadMoreState { LOADED, LOADING, ERROR }
}