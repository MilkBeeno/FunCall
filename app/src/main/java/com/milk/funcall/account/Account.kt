package com.milk.funcall.account

import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.user.data.UserTotalInfoModel
import com.milk.funcall.user.type.Gender
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import kotlinx.coroutines.flow.MutableStateFlow

object Account {

    /** 获取本地保存当前用户的登录状态或更新登录状态 */
    internal val userLoggedFlow = MutableStateFlow(false)
    internal var userLogged: Boolean = false
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_LOGGED_STATE, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(KvKey.ACCOUNT_USER_LOGGED_STATE)
            return field
        }

    /** 获取本地保存当前用户的登录 Token 或更新登录 Token */
    internal var userToken: String = ""
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_ACCESS_TOKEN, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.ACCOUNT_USER_ACCESS_TOKEN)
            return field
        }

    /** 当前用户登录的 ID */
    internal val userIdFlow = MutableStateFlow(0L)
    internal var userId: Long = 0L
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_ID, value)
            field = value
        }
        get() {
            field = KvManger.getLong(KvKey.ACCOUNT_USER_ID)
            return field
        }

    /** 当前用户登录的昵称 */
    internal val userNameFlow = MutableStateFlow("")
    private var userName: String = ""
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_NAME, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.ACCOUNT_USER_NAME)
            return field
        }

    /** 获取当前用户的性别或更新用户性别 */
    internal val userGenderFlow = MutableStateFlow(Gender.Man.value)
    internal var userGender: String = Gender.Man.value
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_GENDER, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.ACCOUNT_USER_GENDER)
            return field
        }

    /** 当前用户登录的头像 */
    internal val userAvatarFlow = MutableStateFlow("")
    private var userAvatar: String = ""
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_AVATAR, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.ACCOUNT_USER_AVATAR)
            return field
        }

    /** 当前用户登录的粉丝数量 */
    internal val userFansFlow = MutableStateFlow(0)
    private var userFans: Int = 0
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_FANS_NUMBER, value)
            field = value
        }
        get() {
            field = KvManger.getInt(KvKey.ACCOUNT_USER_FANS_NUMBER)
            return field
        }

    /** 当前用户登录的关注数量 */
    internal val userFollowsFlow = MutableStateFlow(0)
    private var userFollows: Int = 0
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_FOLLOWS_NUMBER, value)
            field = value
        }
        get() {
            field = KvManger.getInt(KvKey.ACCOUNT_USER_FOLLOWS_NUMBER)
            return field
        }

    /** 当前用户如果是新用户、是否观看过他人个人资料页面图片 */
    internal val newUserViewOtherFlow = MutableStateFlow(false)
    var newUserViewOther: Boolean = false
        set(value) {
            KvManger.put(KvKey.NEW_USER_VIEW_OTHER.plus(userId), value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(KvKey.NEW_USER_VIEW_OTHER)
            return field
        }

    internal fun initialize() {
        if (userLogged) {
            ioScope {
                userLoggedFlow.emit(userLogged)
                userIdFlow.emit(userId)
                userNameFlow.emit(userName)
                userGenderFlow.emit(userGender)
                userAvatarFlow.emit(userAvatar)
                userFansFlow.emit(userFans)
                userFollowsFlow.emit(userFollows)
                newUserViewOtherFlow.emit(newUserViewOther)
            }
        } else userGender = Gender.Man.value
    }

    internal fun logout() {
        ioScope {
            userLogged = false
            userLoggedFlow.emit(false)
            userToken = ""
            userId = 0
            userIdFlow.emit(0)
            userName = ""
            userNameFlow.emit("")
            userGender = Gender.Man.value
            userGenderFlow.emit(Gender.Man.value)
            userAvatar = ""
            userAvatarFlow.emit("")
            userFans = 0
            userFansFlow.emit(0)
            userFollows = 0
            userFollowsFlow.emit(0)
        }
    }

    fun logged(token: String) {
        userLogged = true
        userToken = token
        ioScope { userLoggedFlow.emit(true) }
    }

    fun saveAccountInfo(info: UserTotalInfoModel) {
        ioScope {
            userId = info.userId
            userIdFlow.emit(info.userId)
            userName = info.userName
            userNameFlow.emit(info.userName)
            userGender = info.userGender
            userGenderFlow.emit(info.userGender)
            userAvatar = info.userAvatar
            userAvatarFlow.emit(info.userAvatar)
            userFans = info.userFans
            userFansFlow.emit(info.userFans)
            userFollows = info.userFollows
            userFollowsFlow.emit(info.userFollows)
        }
    }
}