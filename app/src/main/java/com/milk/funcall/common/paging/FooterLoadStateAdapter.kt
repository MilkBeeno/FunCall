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
    private val hasHeader: Boolean = false,
    private val bindTailView: (v: View, state: LoadMoreState) -> Unit? = { _, _ -> }
) : LoadStateAdapter<FooterLoadStateAdapter.FooterViewHolder>() {
    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) {
        val rootView = holder.itemView.apply { visibility = View.GONE }
        when (loadState) {
            is LoadState.Loading -> {
                rootView.visibility = View.VISIBLE
                bindTailView(rootView, LoadMoreState.LOADING)
            }
            is LoadState.Error -> {
                rootView.visibility = View.VISIBLE
                bindTailView(rootView, LoadMoreState.ERROR)
            }
            is LoadState.NotLoading -> {
                val currentItemCount = if (hasHeader) 2 else 1
                if (loadState.endOfPaginationReached
                    && holder.bindingAdapter?.itemCount ?: 0 > currentItemCount
                ) {
                    rootView.visibility = View.VISIBLE
                    bindTailView(rootView, LoadMoreState.LOADED)
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
    enum class LoadMoreState { LOADED, LOADING, ERROR }
}