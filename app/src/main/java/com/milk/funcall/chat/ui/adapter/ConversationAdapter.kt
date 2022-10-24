package com.milk.funcall.chat.ui.adapter

import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.chat.ui.time.convertMessageTime
import com.milk.funcall.chat.ui.view.MessageRedDotView
import com.milk.funcall.common.db.table.ConversationWithUserInfoEntity
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.user.status.Gender
import com.milk.simple.ktx.color

class ConversationAdapter : AbstractPagingAdapter<ConversationWithUserInfoEntity>(
    layoutId = R.layout.item_chat_converstaion,
    diffCallback = object : DiffUtil.ItemCallback<ConversationWithUserInfoEntity>() {
        override fun areItemsTheSame(
            oldItem: ConversationWithUserInfoEntity,
            newItem: ConversationWithUserInfoEntity
        ): Boolean {
            return oldItem.conversation.accountId == newItem.conversation.accountId
                && oldItem.conversation.targetId == newItem.conversation.targetId
                && oldItem.userInfo?.targetId == newItem.userInfo?.targetId
        }

        override fun areContentsTheSame(
            oldItem: ConversationWithUserInfoEntity,
            newItem: ConversationWithUserInfoEntity
        ): Boolean {
            return oldItem.conversation.targetName == newItem.conversation.targetName
                && oldItem.conversation.targetAvatar == newItem.conversation.targetAvatar
                && oldItem.conversation.messageType == newItem.conversation.messageType
                && oldItem.conversation.operationTime == newItem.conversation.operationTime
                && oldItem.conversation.messageContent == newItem.conversation.messageContent
                && oldItem.conversation.isAcceptMessage == newItem.conversation.isAcceptMessage
                && oldItem.conversation.unReadCount == newItem.conversation.unReadCount
                && oldItem.conversation.sendStatus == newItem.conversation.sendStatus
                && oldItem.conversation.putTopTime == newItem.conversation.putTopTime
                && oldItem.userInfo?.targetName == newItem.userInfo?.targetName
                && oldItem.userInfo?.targetAvatar == newItem.userInfo?.targetAvatar
        }
    }
) {
    override fun convert(holder: PagingViewHolder, item: ConversationWithUserInfoEntity) {
        holder.itemView.setBackgroundColor(
            if (item.conversation.putTopTime > 0)
                holder.itemView.context.color(R.color.FFF1EEF5)
            else
                Color.TRANSPARENT
        )
        ImageLoader.Builder()
            .loadAvatar(getTargetAvatar(item), getTargetGender(item))
            .target(holder.getView(R.id.ivUserAvatar))
            .build()
        holder.setText(R.id.tvUserName, getTargetName(item))
        holder.setText(R.id.tvMessage, item.conversation.messageContent)
        holder.setText(R.id.tvTime, item.conversation.operationTime.convertMessageTime())
        holder.getView<MessageRedDotView>(R.id.redDotRootView)
            .updateMessageCount(item.conversation.unReadCount)
    }

    private fun getTargetGender(conversationWithUserInfoEntity: ConversationWithUserInfoEntity): String {
        val userInfo = conversationWithUserInfoEntity.userInfo
        return when {
            userInfo != null -> userInfo.targetGender
            Account.userGender == Gender.Woman.value -> Gender.Man.value
            else -> Gender.Woman.value
        }
    }

    private fun getTargetName(conversationWithUserInfoEntity: ConversationWithUserInfoEntity): String {
        val userInfo = conversationWithUserInfoEntity.userInfo
        val conversation = conversationWithUserInfoEntity.conversation
        return userInfo?.targetName ?: conversation.targetName
    }

    private fun getTargetAvatar(conversationWithUserInfoEntity: ConversationWithUserInfoEntity): String {
        val userInfo = conversationWithUserInfoEntity.userInfo
        val conversation = conversationWithUserInfoEntity.conversation
        return userInfo?.targetAvatar ?: conversation.targetAvatar
    }
}