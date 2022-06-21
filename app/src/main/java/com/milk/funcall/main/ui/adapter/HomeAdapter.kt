package com.milk.funcall.main.ui.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.paging.AbstractLoadStateAdapter
import com.milk.funcall.common.paging.AbstractPageAdapter
import com.milk.funcall.main.data.HomModel

class HomeAdapter : AbstractPageAdapter<HomModel>(
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
    override fun onBindItemView(v: View, item: HomModel?, position: Int) {
        v.findViewById<TextView>(R.id.tvUserName).text = item?.userName
    }

    override fun onBindTailView(v: View, state: AbstractLoadStateAdapter.LoadMoreState) {

    }
}