package com.milk.funcall.chat.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.chat.data.ConversationWithUserInfoModel
import com.milk.funcall.chat.ui.view.MessageRedDotView
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.user.type.Gender

class ConversationAdapter : AbstractPagingAdapter<ConversationWithUserInfoModel>(
    layoutId = R.layout.item_chat_converstaion,
    diffCallback = object : DiffUtil.ItemCallback<ConversationWithUserInfoModel>() {
        override fun areItemsTheSame(
            oldItem: ConversationWithUserInfoModel,
            newItem: ConversationWithUserInfoModel
        ): Boolean {
            return oldItem.conversation.accountId == newItem.conversation.accountId
                    && oldItem.conversation.targetId == newItem.conversation.targetId
        }

        override fun areContentsTheSame(
            oldItem: ConversationWithUserInfoModel,
            newItem: ConversationWithUserInfoModel
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

    override fun convert(holder: PagingViewHolder, item: ConversationWithUserInfoModel) {
        ImageLoader.Builder()
            .loadAvatar(getTargetAvatar(item), getTargetGender(item))
            .target(holder.getView(R.id.ivUserAvatar))
            .build()
        holder.setText(R.id.tvUserName, getTargetName(item))
        holder.setText(R.id.tvMessage, item.conversation.messageContent)
        holder.setText(R.id.tvTime, item.conversation.operationTime.toString())
        holder.getView<MessageRedDotView>(R.id.redDotRootView)
            .updateMessageCount(item.conversation.unReadCount)
    }

    private fun getTargetGender(conversationWithUserInfoModel: ConversationWithUserInfoModel): String {
        val userInfo = conversationWithUserInfoModel.userInfo
        return when {
            userInfo != null -> userInfo.targetGender
            Account.userGender == Gender.Woman.value -> Gender.Man.value
            else -> Gender.Woman.value
        }
    }

    fun getTargetName(conversationWithUserInfoModel: ConversationWithUserInfoModel): String {
        val userInfo = conversationWithUserInfoModel.userInfo
        val conversation = conversationWithUserInfoModel.conversation
        return userInfo?.targetName ?: conversation.targetName
    }

    fun getTargetAvatar(conversationWithUserInfoModel: ConversationWithUserInfoModel): String {
        val userInfo = conversationWithUserInfoModel.userInfo
        val conversation = conversationWithUserInfoModel.conversation
        return userInfo?.targetAvatar ?: conversation.targetAvatar
    }
}