package com.milk.funcall.chat.ui.vm

import androidx.lifecycle.ViewModel
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.db.table.ChatMessageEntity
import com.milk.funcall.common.db.table.UserInfoEntity
import com.milk.funcall.common.paging.LocalPagingSource
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.repo.ReportRepository
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.funcall.user.status.ReportType
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.safeToLong
import kotlinx.coroutines.flow.MutableSharedFlow

class ChatMessageViewModel : ViewModel() {
    internal var userInfoEntity: UserInfoEntity? = null
    internal var userPutTopStatus: Boolean = false
    internal val followedStatusFlow = MutableSharedFlow<Any?>()
    internal val blackUserFlow = MutableSharedFlow<Boolean>()
    internal val reportFlow = MutableSharedFlow<Boolean>()

    /** 私聊列表数据 */
    internal val pagingSource: LocalPagingSource<Int, ChatMessageEntity>
        get() {
            return LocalPagingSource(20, 5) {
                MessageRepository.getChatMessagesByDB(userInfoEntity?.targetId.safeToLong())
            }
        }

    /** 数据库中拉取数据 */
    internal fun getTargetInfoByDB(targetId: Long) = UserInfoRepository.getUserInfoByDB(targetId)

    /** 数据库中不存在数据、从网络上拉取数据 */
    internal fun getTargetInfoByNetwork(targetId: Long) {
        ioScope {
            val apiResponse = UserInfoRepository.getUserInfoByNetwork(targetId)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                setUserInfoEntity(apiResult)
                getConversationPutTopTime()
            }
        }
    }

    /** 将数据保存在内存中 */
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

    /** 更新用户当前最新信息 */
    internal fun updateUserInfoEntity(userInfoEntity: UserInfoEntity) {
        this.userInfoEntity = userInfoEntity
        ioScope { getConversationPutTopTime() }
    }

    /** 获取当前信息的置顶状态 1.置顶时间大于 0 表示已经置顶 2.时间等于 0 表示消息未置顶 */
    private fun getConversationPutTopTime() {
        val result = MessageRepository
            .getConversationPutTopTime(userInfoEntity?.targetId.safeToLong())
        userPutTopStatus = result != null && result > 0
    }

    /** 发送文本消息 */
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

    /** 更新消息未读数量 */
    internal fun updateUnReadCount() {
        ioScope { MessageRepository.updateUnReadCount(userInfoEntity?.targetId.safeToLong()) }
    }

    /** 更新对于当前用户的关注状态 */
    internal fun changeFollowedStatus() {
        ioScope {
            val targetId = userInfoEntity?.targetId.safeToLong()
            val isFollowed = !(userInfoEntity?.targetIsFollowed ?: false)
            val apiResponse =
                UserInfoRepository.changeFollowedStatus(targetId, isFollowed)
            if (apiResponse.success) {
                userInfoEntity?.targetIsFollowed = isFollowed
                LiveEventBus
                    .get<Pair<Boolean, Long>>(EventKey.USER_FOLLOWED_STATUS_CHANGED)
                    .post(Pair(isFollowed, userInfoEntity?.targetId.safeToLong()))
                followedStatusFlow.emit(true)
            } else followedStatusFlow.emit(null)
        }
    }

    /** 拉黑当前用户 */
    internal fun blackUser() {
        ioScope {
            val targetId = userInfoEntity?.targetId.safeToLong()
            val apiResponse = UserInfoRepository.blackUser(targetId)
            if (apiResponse.success) MessageRepository.deleteChatMessage(targetId)
            blackUserFlow.emit(apiResponse.success)
        }
    }

    /** 消息置顶 */
    internal fun putTopChatMessage() {
        ioScope {
            MessageRepository.putTopChatMessage(userInfoEntity?.targetId.safeToLong())
            getConversationPutTopTime()
        }
    }

    /** 取消消息置顶 */
    internal fun unPinChatMessage() {
        ioScope {
            MessageRepository.unPinChatMessage(userInfoEntity?.targetId.safeToLong())
            getConversationPutTopTime()
        }
    }

    internal fun report(userId: Long, type: ReportType) {
        ioScope {
            val response = ReportRepository.report(userId, type)
            reportFlow.emit(response.success)
        }
    }
}