package com.milk.funcall.account.ui.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.db.table.UserInfoEntity
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.user.status.OnlineState
import com.milk.simple.ktx.color

class FansOrFollowsAdapter : AbstractPagingAdapter<UserInfoEntity>(
    layoutId = R.layout.item_fans_or_follows,
    diffCallback = object : DiffUtil.ItemCallback<UserInfoEntity>() {
        override fun areItemsTheSame(oldItem: UserInfoEntity, newItem: UserInfoEntity): Boolean {
            return oldItem.targetId == newItem.targetId
        }

        override fun areContentsTheSame(oldItem: UserInfoEntity, newItem: UserInfoEntity): Boolean {
            return oldItem.targetAvatar == newItem.targetAvatar
                && oldItem.targetName == newItem.targetName
                && oldItem.targetOnline == newItem.targetOnline
                && oldItem.targetImage == newItem.targetImage
                && oldItem.targetVideo == newItem.targetVideo
        }
    }
) {
    override fun convert(holder: PagingViewHolder, item: UserInfoEntity) {
        val isOnline = item.targetOnline == OnlineState.Online.value
        holder.setText(R.id.tvUserName, item.targetName)
        ImageLoader.Builder()
            .request(item.targetImage)
            .placeholder(R.drawable.common_list_default_big)
            .target(holder.getView(R.id.ivUserImage))
            .build()
        ImageLoader.Builder()
            .loadAvatar(item.targetAvatar, item.targetGender)
            .target(holder.getView(R.id.ivUserAvatar))
            .build()
        holder.getView<View>(R.id.vState).setBackgroundResource(
            if (isOnline) {
                R.drawable.shape_home_online_state
            } else {
                R.drawable.shape_home_offline_state
            }
        )
        holder.getView<AppCompatTextView>(R.id.tvState).apply {
            setTextColor(
                if (isOnline) {
                    context.color(R.color.FF58FFD3)
                } else {
                    context.color(R.color.FFEAECF6)
                }
            )
            setText(if (isOnline) R.string.home_online else R.string.home_offline)
        }
    }
}