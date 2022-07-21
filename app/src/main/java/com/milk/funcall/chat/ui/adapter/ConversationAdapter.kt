package com.milk.funcall.chat.ui.adapter

import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.chat.ui.view.MessageRedDotView
import com.milk.funcall.common.mdr.table.ConversationEntity
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder

class ConversationAdapter : AbstractPagingAdapter<ConversationEntity>(
    layoutId = R.layout.item_chat_converstaion,
    diffCallback = object : DiffUtil.ItemCallback<ConversationEntity>() {
        override fun areItemsTheSame(
            oldItem: ConversationEntity,
            newItem: ConversationEntity
        ): Boolean {
            return oldItem.accountId == newItem.accountId && oldItem.targetId == newItem.targetId
        }

        override fun areContentsTheSame(
            oldItem: ConversationEntity,
            newItem: ConversationEntity
        ): Boolean {
            return oldItem.operationTime == newItem.operationTime
                    && oldItem.unReadCount == newItem.unReadCount
                    && oldItem.messageContent == newItem.messageContent
        }
    }
) {
    init {
        addChildClickViewIds(R.id.ivUserAvatar)
    }

    override fun convert(holder: PagingViewHolder, item: ConversationEntity) {
        holder.getView<AppCompatImageView>(R.id.ivUserAvatar)
            .setImageResource(R.drawable.common_default_man)
        holder.setText(R.id.tvUserName, item.targetId.toString())
        holder.setText(R.id.tvMessage, item.messageContent)
        holder.setText(R.id.tvTime, item.operationTime.toString())
        holder.getView<MessageRedDotView>(R.id.redDotRootView)
            .updateMessageCount(item.unReadCount)
    }
}