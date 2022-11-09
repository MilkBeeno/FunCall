package com.milk.funcall.account

import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.status.Gender
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
    internal var userAvatar: String = ""
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
    internal var userFans: Int = 0
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
    internal var userFollows: Int = 0
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_FOLLOWS_NUMBER, value)
            field = value
        }
        get() {
            field = KvManger.getInt(KvKey.ACCOUNT_USER_FOLLOWS_NUMBER)
            return field
        }

    /** 当前用户的个人简介 */
    internal val userBioFlow = MutableStateFlow("")
    private var userBio: String = ""
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_BIO, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.ACCOUNT_USER_BIO)
            return field
        }

    /** 当前用户的个人联系方式 */
    internal val userLinkFlow = MutableStateFlow("")
    private var userLink: String = ""
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_LINK, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.ACCOUNT_USER_LINK)
            return field
        }

    /** 当前用户图片集合存储 */
    internal val userImageListFlow = MutableStateFlow(mutableListOf<String>())
    internal var userImageList: MutableList<String> = mutableListOf()
        set(value) {
            value.forEachIndexed { index, imageUrl ->
                KvManger.put(KvKey.ACCOUNT_USER_IMAGE_LIST + index, imageUrl)
            }
            KvManger.put(KvKey.ACCOUNT_USER_IMAGE_LIST_SIZE, value.size)
            field = value
        }
        get() {
            val imageList = mutableListOf<String>()
            val size = KvManger.getInt(KvKey.ACCOUNT_USER_IMAGE_LIST_SIZE)
            for (index in 0 until size) {
                imageList.add(KvManger.getString(KvKey.ACCOUNT_USER_IMAGE_LIST + index))
            }
            field = imageList
            return field
        }

    /** 当前用户图片集合存储 */
    internal val userVideoFlow = MutableStateFlow("")
    internal var userVideo: String = ""
        set(value) {
            KvManger.put(KvKey.ACCOUNT_USER_VIDEO, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.ACCOUNT_USER_VIDEO)
            return field
        }

    /** - [本地数据不需要和服务器同步] 当前用户是否观看过他人个人资料页面图片 */
    internal val userViewOtherFlow = MutableStateFlow(false)
    internal var userViewOther: Boolean = false
        set(value) {
            KvManger.put(KvKey.USER_VIEW_OTHER.plus(userId), value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(KvKey.USER_VIEW_OTHER.plus(userId))
            return field
        }

    /** 当前账号是否属于订阅状态 */
    internal var userSubscribeFlow = MutableStateFlow(false)
    internal var userSubscribe: Boolean = false
        set(value) {
            KvManger.put(KvKey.USER_SUBSCRIBE_TO_VIEW_OTHER_FREE.plus(userId), value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(KvKey.USER_SUBSCRIBE_TO_VIEW_OTHER_FREE.plus(userId))
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
                userBioFlow.emit(userBio)
                userLinkFlow.emit(userLink)
                userViewOtherFlow.emit(userViewOther)
                userImageListFlow.emit(userImageList)
                userVideoFlow.emit(userVideo)
            }
        }
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
            userBio = ""
            userBioFlow.emit("")
            userLink = ""
            userLinkFlow.emit("")
            userFollowsFlow.emit(0)
            userImageList = mutableListOf()
            userImageListFlow.emit(mutableListOf())
            userVideo = ""
            userVideoFlow.emit("")
            userSubscribe = false
            userSubscribeFlow.emit(false)
        }
    }

    internal fun logged(token: String) {
        userLogged = true
        userToken = token
        ioScope { userLoggedFlow.emit(true) }
    }

    internal fun saveAccountInfo(info: UserInfoModel, registered: Boolean = true) {
        ioScope {
            userId = info.targetId
            userIdFlow.emit(info.targetId)
            userName = info.targetName
            userNameFlow.emit(info.targetName)
            if (registered) userGender = info.targetGender
            userGenderFlow.emit(info.targetGender)
            userAvatar = info.targetAvatar
            userAvatarFlow.emit(info.targetAvatar)
            userFans = info.targetFans
            userFansFlow.emit(info.targetFans)
            userFollows = info.userFollows
            userFollowsFlow.emit(info.userFollows)
            userBio = info.targetBio
            userBioFlow.emit(info.targetBio)
            userLink = info.targetLink
            userLinkFlow.emit(info.targetLink)
            val imageList = info.imageListConvert()
            userImageList = imageList
            userImageListFlow.emit(imageList)
            val videoUrl = info.videoConvert()
            userVideo = videoUrl
            userVideoFlow.emit(videoUrl)
        }
    }
}