package com.milk.funcall.common.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView

open class HeaderLoadStateAdapter(
    @LayoutRes private val headerLayoutId: Int = 0,
    private val bindHeadView: (v: View) -> Unit? = {}
) : LoadStateAdapter<HeaderLoadStateAdapter.HeaderViewHolder>() {
    override fun onBindViewHolder(holder: HeaderViewHolder, loadState: LoadState) {
        bindHeadView(holder.itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): HeaderViewHolder {
        if (headerLayoutId <= 0) return HeaderViewHolder(View(parent.context))
        val footView =
            LayoutInflater.from(parent.context).inflate(headerLayoutId, parent, false)
        return HeaderViewHolder(footView)
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return !(loadState is LoadState.NotLoading && !loadState.endOfPaginationReached)
    }

    inner class HeaderViewHolder(footView: View) : RecyclerView.ViewHolder(footView)
}