package com.milk.funcall.chat.ui.adapter

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.chat.ui.time.convertMessageTime
import com.milk.funcall.chat.status.ChatMessageType
import com.milk.funcall.common.db.table.ChatMessageEntity
import com.milk.funcall.common.db.table.UserInfoEntity
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.MultiTypeDelegate
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.user.status.Gender
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.visible

class ChatMessageAdapter : AbstractPagingAdapter<ChatMessageEntity>(
    diffCallback = object : DiffUtil.ItemCallback<ChatMessageEntity>() {
        override fun areItemsTheSame(
            oldItem: ChatMessageEntity,
            newItem: ChatMessageEntity
        ): Boolean {
            return oldItem.msgLocalUniqueId == newItem.msgLocalUniqueId
        }

        override fun areContentsTheSame(
            oldItem: ChatMessageEntity,
            newItem: ChatMessageEntity
        ): Boolean {
            return oldItem.accountId == newItem.accountId
                && oldItem.targetId == newItem.targetId
                && oldItem.operationTime == newItem.operationTime
                && oldItem.isReadMessage == newItem.isReadMessage
                && oldItem.messageContent == newItem.messageContent
        }
    }) {

    private var userInfoEntity: UserInfoEntity? = null

    init {
        setMultiTypeDelegate(ChatMessageTypeDelegate())
        addChildClickViewIds(R.id.ivPeopleAvatar)
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setUserInfoEntity(userInfoEntity: UserInfoEntity) {
        this.userInfoEntity = userInfoEntity
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.messageType ?: ChatMessageType.TextSend.value
    }

    override fun convert(holder: PagingViewHolder, item: ChatMessageEntity) {
        updateMessageTime(holder, item)
        updatePeopleAvatar(holder, item)
        when (item.messageType) {
            ChatMessageType.TextSend.value -> updateSendTextMessage(holder, item)
            ChatMessageType.TextReceived.value -> updateReceiveTextMessage(holder, item)
        }
    }

    private fun updateMessageTime(holder: PagingViewHolder, item: ChatMessageEntity) {
        val tvOperationTime = holder.getView<AppCompatTextView>(R.id.tvOperationTime)
        when (holder.layoutPosition) {
            0 -> {
                tvOperationTime.visible()
                tvOperationTime.text = item.operationTime.convertMessageTime()
            }
            else -> {
                val lastItem = getNoNullItem(holder.layoutPosition - 1)
                if (item.operationTime - lastItem.operationTime > 4 * 60 * 1000) {
                    tvOperationTime.visible()
                    tvOperationTime.text = item.operationTime.convertMessageTime()
                } else {
                    tvOperationTime.gone()
                    tvOperationTime.text = item.operationTime.convertMessageTime()
                }
            }
        }
    }

    private fun updatePeopleAvatar(holder: PagingViewHolder, item: ChatMessageEntity) {
        val ivPeopleAvatar = holder.getView<AppCompatImageView>(R.id.ivPeopleAvatar)
        val gender = userInfoEntity?.targetGender ?: Gender.Man.value
        val userAvatar = if (item.isAcceptMessage) {
            val userInfoAvatar = userInfoEntity?.targetAvatar
            userInfoAvatar ?: item.targetAvatar
        } else Account.userAvatar
        ImageLoader.Builder()
            .loadAvatar(userAvatar, gender)
            .target(ivPeopleAvatar)
            .build()
    }

    private fun updateSendTextMessage(holder: PagingViewHolder, item: ChatMessageEntity) {
        holder.setText(R.id.tvSendContent, item.messageContent)
    }

    private fun updateReceiveTextMessage(holder: PagingViewHolder, item: ChatMessageEntity) {
        holder.setText(R.id.tvReceiveContent, item.messageContent)
    }

    inner class ChatMessageTypeDelegate : MultiTypeDelegate {
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