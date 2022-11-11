package com.milk.funcall.common.ad

import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.BuildConfig
import com.milk.funcall.account.Account
import com.milk.funcall.app.AppConfig
import com.milk.funcall.common.ad.data.AdModel
import com.milk.funcall.common.ad.data.AdPositionModel
import com.milk.funcall.common.ad.data.AdResponseModel
import com.milk.funcall.common.ad.repo.AdRepository
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.net.json.JsonConvert
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger

object AdConfig {
    private const val AD_CONFIG = "AD_CONFIG_FUN_CALL"
    private val positionMap = mutableMapOf<String, String>()

    /** 是否展示广告判断：0->需要展示广告 1->免个人主页广告 2->免 app 所有广告 */
    internal val adCancelType: Int
        get() {
            return if (Account.userSubscribe) AppConfig.freeAdType else 0
        }

    /** 获取网络中最新的广告信息 */
    internal fun obtain() {
        positionMap.clear()
        ioScope {
            val apiResult = AdRepository().getAdConfig(
                BuildConfig.AD_APP_ID,
                BuildConfig.AD_APP_VERSION,
                BuildConfig.AD_APP_CHANNEL
            )
            val result = apiResult.data
            if (apiResult.success && result != null) {
                updateAdUnitId(result)
                KvManger.put(AD_CONFIG, apiResult)
            } else {
                val storedConfig = KvManger.getString(AD_CONFIG)
                if (storedConfig.isNotBlank()) {
                    JsonConvert.toModel(storedConfig, AdResponseModel::class.java)
                        ?.result?.let { updateAdUnitId(it) }
                }
            }
            LiveEventBus.get<Any?>(EventKey.UPDATE_START_AD_UNIT_ID).post(null)
        }
    }

    /** 保存广告信息 */
    private fun updateAdUnitId(result: MutableList<AdModel>) {
        positionMap.clear()
        result.forEach {
            when (it.code) {
                AdCodeKey.VIEW_USER_LINK ->
                    savePositionId(AdCodeKey.VIEW_USER_LINK, it.positionList)
                AdCodeKey.APP_START ->
                    savePositionId(AdCodeKey.APP_START, it.positionList)
                AdCodeKey.HOME_LIST_FIRST ->
                    savePositionId(AdCodeKey.HOME_LIST_FIRST, it.positionList)
                AdCodeKey.HOME_LIST_SECOND ->
                    savePositionId(AdCodeKey.HOME_LIST_SECOND, it.positionList)
                AdCodeKey.HOME_LIST_THIRD ->
                    savePositionId(AdCodeKey.HOME_LIST_THIRD, it.positionList)
                AdCodeKey.VIEW_USER_VIDEO ->
                    savePositionId(AdCodeKey.VIEW_USER_VIDEO, it.positionList)
                AdCodeKey.VIEW_USER_IMAGE ->
                    savePositionId(AdCodeKey.VIEW_USER_IMAGE, it.positionList)
                AdCodeKey.MAIN_HOME_BOTTOM ->
                    savePositionId(AdCodeKey.MAIN_HOME_BOTTOM, it.positionList)
                AdCodeKey.RECHARGE_BOTTOM_AD ->
                    savePositionId(AdCodeKey.RECHARGE_BOTTOM_AD, it.positionList)
            }
        }
    }

    /** 将广告 ID 保存在 Map 中 */
    private fun savePositionId(positionCode: String, positionList: MutableList<AdPositionModel>?) {
        if (positionList != null && positionList.size > 0) {
            positionMap[positionCode] = positionList[0].posId
        }
    }

    /** 获取广告 ID */
    internal fun getAdvertiseUnitId(key: String): String {
        val position = positionMap[key]
        return if (position?.isNotEmpty() == true) return position else ""
    }

    /** 判断当前是否加载过配置信息 */
    internal fun isLoadedAds(): Boolean {
        return positionMap.isNotEmpty()
    }
}