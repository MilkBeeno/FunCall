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

    /** 当前用户的个人简介 */
    const val ACCOUNT_USER_BIO = "ACCOUNT_USER_PROFILE"

    /** 当前用户的个人联系方式 */
    const val ACCOUNT_USER_LINK = "ACCOUNT_USER_LINK"

    /** 当前用户图片集合存储 */
    const val ACCOUNT_USER_IMAGE_LIST = "ACCOUNT_USER_IMAGE_LIST"

    /** 当前用户视频存储 */
    const val ACCOUNT_USER_VIDEO = "ACCOUNT_USER_VIDEO"

    /** 当前用户图片集合长度 */
    const val ACCOUNT_USER_IMAGE_LIST_SIZE = "ACCOUNT_USER_IMAGE_LIST_SIZE"

    /** 当前用户是否观看过他人个人资料页面图片 */
    const val USER_VIEW_OTHER = "ACCOUNT_NEW_USER_VIEW_OTHER"

    /** 查看用户图片媒体列表 */
    const val DISPLAY_IMAGE_MEDIA_LIST = "DISPLAY_IMAGE_MEDIA_LIST"

    /** 更新个人资料时、删除一张预览图片 */
    const val EDIT_PROFILE_DELETE_IMAGE = "EDIT_PROFILE_DELETE_IMAGE"

    /** 更新个人资料时、删除一个视频 */
    const val EDIT_PROFILE_DELETE_VIDEO = "EDIT_PROFILE_DELETE_VIDEO"

    /** 看过他人联系方式激励视频广告 */
    const val VIEW_OTHER_LINK = "VIEW_OTHER_LINK"

    /** 看过他人视频激励视频广告 */
    const val VIEW_OTHER_VIDEO = "VIEW_OTHER_VIDEO"

    /** 看过他人照片插页广告 */
    const val VIEW_OTHER_IMAGE = "VIEW_OTHER_IMAGE"
}