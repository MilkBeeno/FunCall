package com.milk.funcall.main.ui.adapter

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.MultiTypeDelegate
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.main.enum.ChatMessageType

class ChatMessageAdapter : AbstractPagingAdapter<ChatMessageEntity>(
    diffCallback = object : DiffUtil.ItemCallback<ChatMessageEntity>() {
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
    }) {

    init {
        setMultiTypeDelegate(ChatMessageTypeDelegate())
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.messageType ?: ChatMessageType.TextSend.value
    }

    override fun onConvert(holder: PagingViewHolder, item: ChatMessageEntity) {
        updateMessageTime(holder, item)
        updatePeopleAvatar(holder, item)
        when (item.messageType) {
            ChatMessageType.TextSend.value -> updateSendTextMessage(holder, item)
            ChatMessageType.TextReceived.value -> updateReceiveTextMessage(holder, item)
        }
    }

    private fun updateMessageTime(holder: PagingViewHolder, item: ChatMessageEntity) {
        val tvOperationTime = holder.getView<AppCompatTextView>(R.id.tvOperationTime)
        tvOperationTime.text = item.operationTime.toString()
    }

    private fun updatePeopleAvatar(holder: PagingViewHolder, item: ChatMessageEntity) {
        val ivPeopleAvatar = holder.getView<AppCompatImageView>(R.id.ivPeopleAvatar)
        ivPeopleAvatar.setImageResource(R.drawable.common_default_avatar)
    }

    private fun updateSendTextMessage(holder: PagingViewHolder, item: ChatMessageEntity) {
        holder.setText(R.id.tvSendContent, item.messageContent)
    }

    private fun updateReceiveTextMessage(holder: PagingViewHolder, item: ChatMessageEntity) {
        holder.setText(R.id.tvReceiveContent, item.messageContent)
    }

    inner class ChatMessageTypeDelegate : MultiTypeDelegate() {
        private val viewIdMap = mutableMapOf<Int, Int>()

        init {
            viewIdMap[ChatMessageType.TextSend.value] = R.layout.item_chat_message_text_send
            viewIdMap[ChatMessageType.TextReceived.value] = R.layout.item_chat_message_text_receive
        }

        override fun getItemViewId(viewType: Int): Int {
            return viewIdMap[viewType] ?: R.layout.item_chat_message_text_send
        }
    }
}