package com.milk.funcall.account.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.account.data.BlackedModel
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.common.ui.view.GenderImageView

class BlackedListAdapter : AbstractPagingAdapter<BlackedModel>(
    layoutId = R.layout.item_black_list,
    diffCallback = object : DiffUtil.ItemCallback<BlackedModel>() {
        override fun areItemsTheSame(oldItem: BlackedModel, newItem: BlackedModel): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: BlackedModel, newItem: BlackedModel): Boolean {
            return oldItem.userName == newItem.userName
                && oldItem.userAvatar == newItem.userAvatar
                && oldItem.userGender == newItem.userGender
        }
    }
) {
    init {
        addChildClickViewIds(R.id.ivUserAvatar)
    }

    override fun convert(holder: PagingViewHolder, item: BlackedModel) {
        holder.setText(R.id.tvUserName, item.userName)
        ImageLoader.Builder()
            .loadAvatar(item.userAvatar, item.userGender)
            .target(holder.getView(R.id.ivUserAvatar))
            .build()
        holder.getView<GenderImageView>(R.id.ivUserGender).updateGender(item.userGender)
    }
}