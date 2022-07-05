package com.milk.funcall.account.ui.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.imageLoad.loadAvatar
import com.milk.funcall.common.imageLoad.loadSimple
import com.milk.funcall.common.mdr.table.UserInfoEntity
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.user.type.OnlineState
import com.milk.simple.ktx.color

class FansAdapter : AbstractPagingAdapter<UserInfoEntity>(
    layoutId = R.layout.item_fans,
    diffCallback = object : DiffUtil.ItemCallback<UserInfoEntity>() {
        override fun areItemsTheSame(oldItem: UserInfoEntity, newItem: UserInfoEntity): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserInfoEntity, newItem: UserInfoEntity): Boolean {
            return oldItem.userAvatar == newItem.userAvatar
                    && oldItem.userName == newItem.userName
                    && oldItem.userOnline == newItem.userOnline
                    && oldItem.userImage == newItem.userImage
                    && oldItem.userVideo == newItem.userVideo
        }
    }
) {
    override fun convert(holder: PagingViewHolder, item: UserInfoEntity) {
        val isOnline = item.userOnline == OnlineState.Online.value
        holder.setText(R.id.tvUserName, item.userName)
        holder.getView<AppCompatImageView>(R.id.ivUserImage)
            .loadSimple(item.userImage)
        holder.getView<AppCompatImageView>(R.id.ivUserAvatar)
            .loadAvatar(item.userAvatar)
        holder.getView<View>(R.id.vState).setBackgroundResource(
            if (isOnline) R.drawable.shape_home_online_state else R.drawable.shape_home_offline_state
        )
        holder.getView<AppCompatTextView>(R.id.tvState).apply {
            setTextColor(
                if (isOnline) context.color(R.color.FF58FFD3) else context.color(R.color.FFEAECF6)
            )
            setText(if (isOnline) R.string.home_online else R.string.home_offline)
        }
    }
}