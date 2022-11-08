package com.milk.funcall.common.constrant

object FirebaseKey {
    /** 登录处埋点 */
    const val FIRST_OPEN = "first_open"
    const val FIRST_OPEN_HOME_PAGE = "first_open_home_page"
    const val LOGINS_WITH_GUEST = "logins_with_guest"
    const val LOGINS_WITH_GUEST_SUCCESS = "logins_with_guest_Success"
    const val LOGINS_WITH_GUEST_FAIL = "logins_with_guest_fail"
    const val LOGINS_WITH_GOOGLE = "logins_with_Google"
    const val LOGINS_WITH_GOOGLE_SUCCESS = "logins_with_Google_Success"
    const val LOGINS_WITH_GOOGLE_FAIL = "logins_with_Google_fail"
    const val LOGINS_WITH_FB = "logins_with_fb"
    const val LOGINS_WITH_FB_SUCCESS = "logins_with_fb_Success"
    const val LOGINS_WITH_FB_FAIL = "logins_with_fb_fail"
    const val MAX_REGISTRATIONS_REACHED_SHOW = "max_registrations_reached_show"
    const val CLICK_LOGIN_PAGE_PRIVACY_POLICY = "Click_Login_Page_Privacy_Policy"
    const val CLICK_LOGIN_PAGE_USER_AGREEMENT = "Click_login_page_User_Agreement"
    const val LOGIN_SUCCESSFUL = "login_successful"
    const val LOGIN_FAIL = "login_fail"

    /** 选择性别埋点 */
    const val OPEN_SELECT_GENDER_PAGE = "Open_Select_Gender_page"
    const val CLICK_GIRL = "Click_girl"

    /** 填写基本资料页面 */
    const val OPEN_FILL_IN_THE_INFORMATION_PAGE = "Open_fill_in_the_information_page"
    const val CHANGE_NAME = "change_name"
    const val CLICK_DEFAULT_AVATAR = "Click_default_avatar"

    /** 我的页面埋点 */
    const val CLICK_MY_BUTTON = "Click_my_button"
    const val CLICK_ON_MY_PAGE_LOGIN_PORTAL = "Click_on_My_Page_Login_Portal"
    const val CLICK_THE_LOG_OUT = "Click_the_log_out"
    const val LOG_OUT_SUCCESS = "log_out_Success"
    const val CLICK_BLACKLIST = "Click_blacklist"
    const val CLICK_THE_FOLLOW = "Click_the_Follow"
    const val CLICK_ABOUT_OUR = "Click_about_our"
    const val CLICK_THE_FAN = "Click_the_fan"
    const val CLICK_ON_EDIT_PROFILE = "Click_on_edit_profile"

    /** 黑名单、粉丝、关注埋点 */
    const val BLACKLIST_SHOW = "blacklist_show"
    const val FOLLOW_SHOW = "Follow_show"
    const val FAN_SHOW = "fan_show"
    const val CLICK_FAN_AVATAR = "Click_fan_avatar"

    /** 关于我们页面埋点 */
    const val ABOUT_OUR_SHOW = "about_our_show"
    const val CLICK_USER_AGREEMENT = "Click_User_Agreement"
    const val OPEN_USER_AGREEMENT_PAGE = "Open_User_Agreement_page"
    const val CLICK_PRIVACY_POLICY = "Click_Privacy_Policy"
    const val OPEN_AGREEMENT_PAGE = "Open_agreement_page"

    /** 编辑个人资料埋点 */
    const val OPEN_EDIT_PAGE = "Open_edit_page"
    const val CLICK_ON_THE_NICKNAME_BOX = "Click_on_the_Nickname_box"
    const val CLICK_ON_AVATAR = "Click_on_avatar"
    const val CLICK_ON_THE_CONTACTION = "Click_on_the_contaction"
    const val CLICK_ON_PERSONAL_INTRODUCTION = "Click_on_personal_introduction"
    const val CLICK_UPLOAD_IMAGE_ICON = "Click_upload_image_icon"
    const val UPLOAD_IMAGE_FAIL = "upload_image_fail"

    /** 消息列表页面埋点 */
    const val CLICK_CHAT_WITH_OTHER = "Click_chat_with_other"
    const val ENTER_MESSAGE = "enter_message"
    const val CLICK_THE_STICKY = "Click_the_sticky"
    const val CLICK_THE_DELETE = "Click_the_delete"
    const val CLICK_TO_UNPIN = "Click_to_unpin"

    /** 对话页面埋点 */
    const val CLICK_TOP_ON_CHAT_PAGE = "Click_top_on_chat_page"
    const val CLICK_UNPIN__ON_CHAT_PAGE = "Click_Unpin__on_chat_page"
    const val CLICK_BLACKOUT_ON_CHAT_PAGE = "Click_blackout_on_chat_page"
    const val CLICK_FOLLOW_ON_CHAT_PAGE = "Click_Follow_on_chat_page"

    /** 个人主页埋点 */
    const val CLICK_MESSAGE_ON_PROFILE_PAGE = "Click_Message_on_Profile_page"
    const val CLICK_MESSAGE_VIEW_IMAGE_PAGE = "Click_Message_View_Image_Page"
    const val CLICK_MESSAGE_VIEW_VIDEO_PAGE = "Click_Message_View_video_Page"
    const val CLICK_THE_NEXT = "click_the_next"
    const val CLICK_PHOTO = "click_photo"
    const val SHOW_CONTACT_POPUP_DOUBLE_CHECK = "Show_Contact_Popup_Double_Check"
    const val CLICK_CONFIRM_CONTACT_DOUBLE_CHECK = "Click_Confirm_Contact_Double_Check"
    const val CLICK_CANCEL_SHOW_CONTACT_DOUBLE_CHECK = "Click_Cancel_Show_Contact_double_check"
    const val SHOW_FIRST_UNLOCK_VIDEO_OR_PICTURE = "Show_first_unlock_video_or_picture"

    /** 发起广告请求的次数_app启动插页广告 */
    const val MAKE_AN_AD_REQUEST = "Make_an_ad_request"

    /** 广告请求成功_app启动插页广告 */
    const val AD_REQUEST_SUCCEEDED = "Ad_request_succeeded"

    /** 广告请求失败（需要统计原因）_app启动插页广告 */
    const val AD_REQUEST_FAILED = "Ad_request_failed"

    /** 广告展示成功_app启动插页广告 */
    const val THE_AD_SHOW_SUCCESS = "The_ad_show_success"

    /** 广告展示失败（需要统计原因）_app启动插页广告 */
    const val AD_SHOW_FAILED = "Ad_show_failed"

    /** 点击广告位_app启动插页广告 */
    const val CLICK_AD = "click_ad"

    /** 发起广告请求_首页瀑布流原生广告 */
    const val MAKE_AN_AD_REQUEST_1 = "Make_an_ad_request_1"

    /** 广告请求成功_首页瀑布流原生广告 */
    const val AD_REQUEST_SUCCEEDED_1 = "Ad_request_succeeded_1"

    /** 广告请求失败（需要统计原因）_首页瀑布流原生广告 */
    const val AD_REQUEST_FAILED_1 = "Ad_request_failed_1"

    /** 广告展示成功_首页瀑布流原生广告 */
    const val THE_AD_SHOW_SUCCESS_1 = "The_ad_show_success_1"

    /** 广告展示失败（需要统计原因）_首页瀑布流原生广告 */
    const val AD_SHOW_FAILED_1 = "Ad_show_failed_1"

    /** 点击广告位_首页瀑布流原生广告 */
    const val CLICK_AD_1 = "click_ad_1"

    /** 发起广告请求_首页瀑布流原生广告-2 */
    const val MAKE_AN_AD_REQUEST_2 = "Make_an_ad_request_2"

    /** 广告请求成功_首页瀑布流原生广告-2 */
    const val AD_REQUEST_SUCCEEDED_2 = "Ad_request_succeeded_2"

    /** 广告请求失败（需要统计原因）_首页瀑布流原生广告-2 */
    const val AD_REQUEST_FAILED_2 = "Ad_request_failed_2"

    /** 广告展示成功_首页瀑布流原生广告-2 */
    const val THE_AD_SHOW_SUCCESS_2 = "The_ad_show_success_2"

    /** 广告展示失败（需要统计原因）_首页瀑布流原生广告-2 */
    const val AD_SHOW_FAILED_2 = "Ad_show_failed_2"

    /** 点击广告位_首页瀑布流原生广告-2 */
    const val CLICK_AD_2 = "click_ad_2"

    /** 发起广告请求_首页瀑布流原生广告-3 */
    const val MAKE_AN_AD_REQUEST_3 = "Make_an_ad_request_3"

    /** 广告请求成功_首页瀑布流原生广告-3 */
    const val AD_REQUEST_SUCCEEDED_3 = "Ad_request_succeeded_3"

    /** 广告请求失败（需要统计原因）_首页瀑布流原生广告-3 */
    const val Ad_request_failed_3 = "Ad_request_failed_3"

    /** 广告展示成功_首页瀑布流原生广告-3 */
    const val THE_AD_SHOW_SUCCESS_3 = "The_ad_show_success_3"

    /** 广告展示失败（需要统计原因）_首页瀑布流原生广告-3 */
    const val AD_SHOW_FAILED_3 = "Ad_show_failed_3"

    /** 点击广告位_首页瀑布流原生广告-3 */
    const val CLICK_AD_3 = "click_ad_3"

    /** 发起广告请求_首页底部的横幅广告 */
    const val MAKE_AN_AD_REQUEST_4 = "Make_an_ad_request_4"

    /** 广告请求成功_首页底部的横幅广告 */
    const val AD_REQUEST_SUCCEEDED_4 = "Ad_request_succeeded_4"

    /** 广告请求失败（需要统计原因）_首页底部的横幅广告 */
    const val AD_REQUEST_FAILED_4 = "Ad_request_failed_4"

    /** 广告展示成功_首页底部的横幅广告 */
    const val THE_AD_SHOW_SUCCESS_4 = "The_ad_show_success_4"

    /** 广告展示失败（需要统计原因）_首页底部的横幅广告 */
    const val AD_SHOW_FAILED_4 = "Ad_show_failed_4"

    /** 点击广告位_首页底部的横幅广告 */
    const val CLICK_AD_4 = "click_ad_4"

    /** 发起广告请求_个人主页查看更多照片插页广告 */
    const val MAKE_AN_AD_REQUEST_5 = "Make_an_ad_request_5"

    /** 广告请求成功_个人主页查看更多照片插页广告 */
    const val AD_REQUEST_SUCCEEDED_5 = "Ad_request_succeeded_5"

    /** 广告请求失败（需要统计原因）_个人主页查看更多照片插页广告 */
    const val AD_REQUEST_FAILED_5 = "Ad_request_failed_5"

    /** 广告展示成功_个人主页查看更多照片插页广告 */
    const val THE_AD_SHOW_SUCCESS_5 = "The_ad_show_success_5"

    /** 广告展示失败（需要统计原因）_个人主页查看更多照片插页广告 */
    const val AD_SHOW_FAILED_5 = "Ad_show_failed_5"

    /** 点击广告_个人主页查看更多照片插页广告 */
    const val CLICK_AD_5 = "click_ad_5"

    /** 发起广告请求_个人主页查看联系方式激励视频 */
    const val MAKE_AN_AD_REQUEST_6 = "Make_an_ad_request_6"

    /** 广告请求成功_个人主页查看联系方式激励视频 */
    const val AD_REQUEST_SUCCEEDED_6 = "Ad_request_succeeded_6"

    /** 广告请求失败（需要统计原因）_个人主页查看联系方式激励视频 */
    const val AD_REQUEST_FAILED_6 = "Ad_request_failed_6"

    /** 广告展示成功_个人主页查看联系方式激励视频 */
    const val THE_AD_SHOW_SUCCESS_6 = "The_ad_show_success_6"

    /** 广告展示失败（需要统计原因）_个人主页查看联系方式激励视频 */
    const val AD_SHOW_FAILED_6 = "Ad_show_failed_6"

    /** 点击广告_个人主页查看联系方式激励视频 */
    const val CLICK_AD_6 = "click_ad_6"

    /** 进入登录页面 */
    const val OPEN_LOGINS_PAGE = "open_logins_page"

    /** 发起广告请求的次数_订阅页面原生广告 */
    const val MAKE_AN_AD_REQUEST_7 = "Make_an_ad_request_7"

    /** 广告请求成功的次数_订阅页面原生广告 */
    const val AD_REQUEST_SUCCEEDED_7 = "Ad_request_succeeded_7"

    /** 广告请求失败的次数_订阅页面原生广告 */
    const val AD_REQUEST_FAILED_7 = "Ad_request_failed_7"

    /**  广告请求成功的次数_订阅页面原生广告 */
    const val THE_AD_SHOW_SUCCESS_7 = "The_ad_show_success_7"

    /** 广告展示失败的次数_订阅页面原生广告 */
    const val AD_SHOW_FAILED_7 = "Ad_show_failed_7"

    /** 点击广告次数_订阅页面原生广告 */
    const val CLICK_AD_7 = "click_ad_7"

    /** 点击免费解锁按钮进行解锁联系方式 */
    const val CLICK_FREE_UNLOCK_CONTACT = "Click_Free_Unlock_Contact"

    /** 点击广告解锁按钮进行解锁联系方式 */
    const val CLICK_AD_TO_UNLOCK_THE_CONTACT_INFORMATION =
        "Click_ad_to_unlock_the_contact_information"

    /** 点击免费解锁按钮进行解锁相册 */
    const val CLICK_UNLOCK_PHOTO_ALBUM_FOR_FREE = "Click_unlock_photo_album_for_free"

    /** 点击广告解锁按钮进行解锁相册 */
    const val CLICK_THE_AD_TO_UNLOCK_THE_ALBUM = "Click_the_ad_to_unlock_the_album"

    /** 点击我的页面的订阅入口 */
    const val CLICK_THE_SUBSCRIPTION_PORTAL = "Click_the_subscription_portal"

    /** 用户进入到订阅页面 */
    const val SUBSCRIPTIONS_PAGE_SHOW = "Subscriptions_page_Show"

    /** 用户选择使用按周订阅 */
    const val CLICK_SUBSCRIBE_BY_WEEK = "Click_subscribe_by_week"

    /** 用户选择使用按年订阅 */
    const val CLICK_SUBSCRIBE_BY_YEAR = "Click_Subscribe_by_Year"

    /** 用户订阅成功提示 */
    const val SUBSCRIPTION_SUCCESS_SHOW = "Subscription_Success_Show"

    /** 用户已经订阅后重复点击 */
    const val SUBSCRIPTION_ALERTS_SHOW = "Subscription_Alerts_Show"

    /** 用户点击率push后进入到某个女用户的个人主页，这里需要注意用户来源 */
    const val CLICK_PROMOTE_PUSH = "Click_Promote_push"

    /** 进入他人个人页面后弹出的申请开启个人主页的 */
    const val OPEN_NOTIFICATION_REQUEST_POPUP_SHOW = "Open_notification_request_popup_Show"

    /** 点击 开启通知弹窗 关闭按钮 */
    const val CLICK_OPEN_NOTIFICATION_POPUP_CANCEL = "Click_open_notification_popup_cancel"

    /** 点击 开启通知弹窗 确认按钮 */
    const val CLICK_OPEN_NOTIFICATION_POPUP_CONFIRM = "Click_open_notification_popup_Confirm"
}
