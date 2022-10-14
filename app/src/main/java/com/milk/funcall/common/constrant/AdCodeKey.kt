package com.milk.funcall.common.constrant

object AdCodeKey {
    /** 测试的设备号 */
    internal val testDeviceIds = listOf("114942fb-55a8-4ebc-abe0-3f6ff77eddb5")

    /** 个人主页查看联系方式激励视频  */
    internal const val VIEW_USER_LINK = "87654321"

    /** app启动插页广告 */
    internal const val APP_START = "87654322"

    /** 第一个首页瀑布流原生广告 */
    internal const val HOME_LIST_FIRST = "87654323"

    /** 第二个首页瀑布流原生广告 */
    internal const val HOME_LIST_SECOND = "87654327"

    /** 第三个首页瀑布流原生广告 */
    internal const val HOME_LIST_THIRD = "87654328"

    /** 个人主页查看用户视频插页广告  */
    internal const val VIEW_USER_VIDEO = "87654324"

    /** 个人主页查看更多照片插页广告  */
    internal const val VIEW_USER_IMAGE = "87654325"

    /** 首页底部的横幅广告  */
    internal const val MAIN_HOME_BOTTOM = "87654326"

    /******************************* 以下是广告开关配置保存的 KEY ************************************/

    /** 广告总开关 */
    internal const val AD_MASTER_SWITCH = "Ad_master_switch"

    /** 启动页面广告是否开启 */
    internal const val APP_LAUNCH_INTERSTITIAL = "app_launch_interstitial"

    /** 首页瀑布流广告一是否开启 */
    internal const val HOMEPAGE_WATERFALL_ADS_FIRST = "Homepage_waterfall_Ads_1"

    /** 首页瀑布流广告一是否开启 */
    internal const val HOMEPAGE_WATERFALL_ADS_SECOND = "Homepage_Waterfall_Ads_2"

    /** 首页瀑布流广告一是否开启 */
    internal const val HOMEPAGE_WATERFALL_ADS_THIRD = "Homepage_Waterfall_Ads_3"

    /** 底部横幅广告是否开启 */
    internal const val BANNER_OF_HOMEPAGE = "Banner_of_homepage"

    /** 个人主页查看用户联系方式激励视频广告是否开启 */
    internal const val VIEW_CONTACT_VIDEO_ADS = "View_Contact_Video_Ads"

    /** 个人主页查看用户视频激励视频广告是否开启 */
    internal const val VIEW_USER_VIDEO_ADS = "View_User_Video_Ads"

    /** 个人主页查看用户图片激励视频广告是否开启 */
    internal const val VIEW_PHOTO_ADS = "view_photo_Ads"
}