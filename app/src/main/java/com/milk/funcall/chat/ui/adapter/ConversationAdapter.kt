package com.milk.funcall.chat.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.chat.data.ConversationWithUserInfo
import com.milk.funcall.chat.ui.view.MessageRedDotView
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder

class ConversationAdapter : AbstractPagingAdapter<ConversationWithUserInfo>(
    layoutId = R.layout.item_chat_converstaion,
    diffCallback = object : DiffUtil.ItemCallback<ConversationWithUserInfo>() {
        override fun areItemsTheSame(
            oldItem: ConversationWithUserInfo,
            newItem: ConversationWithUserInfo
        ): Boolean {
            return oldItem.conversation.accountId == newItem.conversation.accountId
                    && oldItem.conversation.targetId == newItem.conversation.targetId
        }

        override fun areContentsTheSame(
            oldItem: ConversationWithUserInfo,
            newItem: ConversationWithUserInfo
        ): Boolean {
            return oldItem.conversation.operationTime == newItem.conversation.operationTime
                    && oldItem.conversation.unReadCount == newItem.conversation.unReadCount
                    && oldItem.conversation.messageContent == newItem.conversation.messageContent
                    && oldItem.userInfo?.targetName == newItem.userInfo?.targetName
                    && oldItem.userInfo?.targetAvatar == newItem.userInfo?.targetAvatar
        }
    }
) {
    init {
        addChildClickViewIds(R.id.ivUserAvatar)
    }

    override fun convert(holder: PagingViewHolder, item: ConversationWithUserInfo) {
        ImageLoader.Builder()
            .loadAvatar(getTargetAvatar(item))
            .target(holder.getView(R.id.ivUserAvatar))
            .build()
        holder.setText(R.id.tvUserName, getTargetName(item))
        holder.setText(R.id.tvMessage, item.conversation.messageContent)
        holder.setText(R.id.tvTime, item.conversation.operationTime.toString())
        holder.getView<MessageRedDotView>(R.id.redDotRootView)
            .updateMessageCount(item.conversation.unReadCount)
    }

    fun getTargetName(conversationWithUserInfo: ConversationWithUserInfo): String {
        val userInfo = conversationWithUserInfo.userInfo
        val conversation = conversationWithUserInfo.conversation
        return userInfo?.targetName ?: conversation.targetName
    }

    fun getTargetAvatar(conversationWithUserInfo: ConversationWithUserInfo): String {
        val userInfo = conversationWithUserInfo.userInfo
        val conversation = conversationWithUserInfo.conversation
        return userInfo?.targetAvatar ?: conversation.targetAvatar
    }
}