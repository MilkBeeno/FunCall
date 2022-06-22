package com.milk.funcall.main.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.FooterLoadStateAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.main.data.HomModel

class HomeAdapter : AbstractPagingAdapter<HomModel>(
    layoutId = R.layout.item_hone,
    diffCallback = object : DiffUtil.ItemCallback<HomModel>() {
        override fun areItemsTheSame(oldItem: HomModel, newItem: HomModel): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: HomModel, newItem: HomModel): Boolean {
            return oldItem.userId == newItem.userId
                    && oldItem.userName == newItem.userName
        }
    }
) {
    override fun convert(holder: PagingViewHolder, item: HomModel) {
        holder.setText(R.id.tvUserName, item.userName)
    }

    override fun obtainFooterAdapter(hasHeader: Boolean): FooterLoadStateAdapter {
        return FooterLoadStateAdapter(R.layout.view_paging_foot, hasHeader)
    }
}