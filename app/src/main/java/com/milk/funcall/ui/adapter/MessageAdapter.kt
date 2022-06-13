package com.milk.funcall.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.data.MessageEntity
import com.milk.common.paging.AbstractPagingAdapter
import com.milk.common.paging.PagingViewHolder

class MessageAdapter : AbstractPagingAdapter<MessageEntity>(R.layout.item_message, diffCallback) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MessageEntity>() {
            override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
                return oldItem.messageUniqueId == newItem.messageUniqueId
            }

            override fun areContentsTheSame(
                oldItem: MessageEntity,
                newItem: MessageEntity
            ): Boolean {
                return oldItem.userId == newItem.userId
                        && oldItem.targetId == newItem.targetId
                        && oldItem.targetName == newItem.targetName
                        && oldItem.targetImage == newItem.targetImage
                        && oldItem.operationTime == newItem.operationTime
            }
        }
    }

    override fun onConvert(holder: PagingViewHolder, item: MessageEntity) {
        holder.setText(R.id.user_name, item.messageUniqueId)
    }
}