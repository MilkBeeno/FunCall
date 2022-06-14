package com.milk.funcall.main.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.main.data.ChatMessageEntity

class ChatMessageAdapter :
    AbstractPagingAdapter<ChatMessageEntity>(R.layout.item_message, diffCallback) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ChatMessageEntity>() {
            override fun areItemsTheSame(
                oldItem: ChatMessageEntity,
                newItem: ChatMessageEntity
            ): Boolean {
                return oldItem.messageUniqueId == newItem.messageUniqueId
            }

            override fun areContentsTheSame(
                oldItem: ChatMessageEntity,
                newItem: ChatMessageEntity
            ): Boolean {
                return oldItem.userId == newItem.userId
                        && oldItem.targetId == newItem.targetId
                        && oldItem.targetName == newItem.targetName
                        && oldItem.targetImage == newItem.targetImage
                        && oldItem.operationTime == newItem.operationTime
                        && oldItem.isReadMessage == newItem.isReadMessage
            }
        }
    }

    override fun onConvert(holder: PagingViewHolder, item: ChatMessageEntity) {
        holder.setText(R.id.user_name, item.messageUniqueId)
    }
}