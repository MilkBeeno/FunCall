package com.milk.funcall.firebase.constant

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
}