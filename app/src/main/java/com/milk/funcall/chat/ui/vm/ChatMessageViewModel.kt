package com.milk.funcall.chat.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.paging.LocalPagingSource
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.simple.ktx.ioScope

class ChatMessageViewModel : ViewModel() {
    private var targetId: Long = 0
    private var targetName: String = ""
    private var targetAvatar: String = ""
    val pagingSource: LocalPagingSource<Int, ChatMessageEntity>
        get() {
            return LocalPagingSource(
                pageSize = 20,
                prefetchDistance = 5,
                pagingSourceFactory = {
                    MessageRepository.getChatMessagesByDB(targetId)
                }
            )
        }

    internal fun getTargetInfoByDB(targetId: Long) = UserInfoRepository.getUserInfoByDB(targetId)

    internal fun getTargetInfoByNetwork(targetId: Long) {
        ioScope { UserInfoRepository.getUserInfoByNetwork(targetId) }
    }

    internal fun updateTargetUser(targetId: Long, targetName: String, targetAvatar: String) {
        this.targetId = targetId
        this.targetName = targetName
        this.targetAvatar = targetAvatar
    }

    internal fun sendTextChatMessage(messageContent: String) {
        ioScope {
            MessageRepository.sendTextChatMessage(
                targetId = targetId,
                targetName = targetName,
                targetAvatar = targetAvatar,
                messageContent = messageContent
            )
        }
    }

    internal fun updateUnReadCount() {
        ioScope { MessageRepository.updateUnReadCount(targetId) }
    }
}