package com.milk.funcall.main.ui.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.imageLoad.loadCirclePicture
import com.milk.funcall.common.imageLoad.loadRoundPicture
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.FooterLoadStateAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.main.data.HomModel

class HomeAdapter : AbstractPagingAdapter<HomModel>(
    layoutId = R.layout.item_hone_big,
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
        holder.getView<AppCompatImageView>(R.id.ivUserImage)
            .loadRoundPicture(item.userImage, 10f)
        holder.getView<AppCompatImageView>(R.id.ivUserAvatar)
            .loadCirclePicture(item.userAvatar)
        holder.getView<View>(R.id.vState).setBackgroundResource(
            if (item.isOnline)
                R.drawable.shape_home_online_state
            else
                R.drawable.shape_home_offline_state
        )
    }

    override fun obtainFooterAdapter(hasHeader: Boolean): FooterLoadStateAdapter {
        return FooterLoadStateAdapter(R.layout.layout_paging_foot, hasHeader)
    }
}