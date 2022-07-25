package com.milk.funcall.chat.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.mdr.table.UserInfoEntity
import com.milk.funcall.common.paging.LocalPagingSource
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.safeToLong
import kotlinx.coroutines.flow.MutableSharedFlow

class ChatMessageViewModel : ViewModel() {
    var userInfoEntity: UserInfoEntity? = null
    val followedStatusFlow = MutableSharedFlow<Any?>()
    val blackUserFlow = MutableSharedFlow<Boolean>()
    val pagingSource: LocalPagingSource<Int, ChatMessageEntity>
        get() {
            return LocalPagingSource(20, 5) {
                MessageRepository.getChatMessagesByDB(userInfoEntity?.targetId.safeToLong())
            }
        }

    internal fun getTargetInfoByDB(targetId: Long) = UserInfoRepository.getUserInfoByDB(targetId)

    internal fun getTargetInfoByNetwork(targetId: Long) {
        ioScope {
            val apiResponse = UserInfoRepository.getUserInfoByNetwork(targetId)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null)
                setUserInfoEntity(apiResult)
        }
    }

    private fun setUserInfoEntity(userInfo: UserInfoModel) {
        userInfoEntity = UserInfoEntity()
        userInfoEntity?.accountId = Account.userId
        userInfoEntity?.targetId = userInfo.targetId
        userInfoEntity?.targetName = userInfo.targetName
        userInfoEntity?.targetAvatar = userInfo.targetAvatar
        userInfoEntity?.targetGender = userInfo.targetGender
        userInfoEntity?.targetImage = userInfo.targetImage
        userInfoEntity?.targetVideo = userInfo.targetVideo
        userInfoEntity?.targetOnline = userInfo.targetOnline
        userInfoEntity?.targetIsFollowed = userInfo.targetIsFollowed
        userInfoEntity?.targetIsBlacked = userInfo.targetIsBlacked
    }

    internal fun updateUserInfoEntity(userInfoEntity: UserInfoEntity) {
        this.userInfoEntity = userInfoEntity
    }

    internal fun sendTextChatMessage(messageContent: String) {
        ioScope {
            MessageRepository.sendTextChatMessage(
                userInfoEntity?.targetId.safeToLong(),
                userInfoEntity?.targetName.toString(),
                userInfoEntity?.targetAvatar.toString(),
                messageContent
            )
        }
    }

    internal fun updateUnReadCount() {
        ioScope { MessageRepository.updateUnReadCount(userInfoEntity?.targetId.safeToLong()) }
    }

    internal fun changeFollowedStatus() {
        ioScope {
            val targetId = userInfoEntity?.targetId.safeToLong()
            val isFollowed = !(userInfoEntity?.targetIsFollowed ?: false)
            val apiResponse =
                UserInfoRepository.changeFollowedStatus(targetId, isFollowed)
            if (apiResponse.success) userInfoEntity?.targetIsFollowed = isFollowed
            followedStatusFlow.emit(null)
        }
    }

    internal fun blackUser() {
        ioScope {
            val targetId = userInfoEntity?.targetId.safeToLong()
            val apiResponse = UserInfoRepository.blackUser(targetId)
            if (apiResponse.success) MessageRepository.deleteChatMessage(targetId)
            blackUserFlow.emit(apiResponse.success)
        }
    }
}