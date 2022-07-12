package com.milk.funcall.common.constrant

object KvKey {
    /** 当前用户的 ID */
    const val ACCOUNT_USER_ID = "ACCOUNT_USER_ID"

    /** 当前用户的昵称 */
    const val ACCOUNT_USER_NAME = "ACCOUNT_USER_NAME"

    /** 当前用户或游客的性别状态 */
    const val ACCOUNT_USER_GENDER = "ACCOUNT_USER_GENDER"

    /** 当前用户的头像 */
    const val ACCOUNT_USER_AVATAR = "ACCOUNT_USER_AVATAR"

    /** 用户登录的状态 true 已登录 false 未登录 */
    const val ACCOUNT_USER_LOGGED_STATE = "ACCOUNT_USER_LOGGED_STATE"

    /** 用户通过 Google、Facebook、Device 登录后获取到有效的 AccessToken */
    const val ACCOUNT_USER_ACCESS_TOKEN = "ACCOUNT_USER_ACCESS_TOKEN"

    /** 当前用户粉丝数量 */
    const val ACCOUNT_USER_FANS_NUMBER = "ACCOUNT_USER_FANS_NUMBER"

    /** 当前用户关注数量 */
    const val ACCOUNT_USER_FOLLOWS_NUMBER = "ACCOUNT_USER_FOLLOWS_NUMBER"

    /** 当前用户如果是新用户、是否观看过他人个人资料页面图片 */
    const val NEW_USER_VIEW_OTHER = "ACCOUNT_NEW_USER_VIEW_OTHER"

    /** 查看用户图片媒体列表 */
    const val DISPLAY_IMAGE_MEDIA_LIST = "DISPLAY_IMAGE_MEDIA_LIST"
}