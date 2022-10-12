package com.milk.funcall.common.ad

import android.content.Context
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.BaseApplication
import com.milk.funcall.BuildConfig
import com.milk.funcall.R
import com.milk.funcall.common.ad.constant.AdCodeKey
import com.milk.funcall.common.ad.data.AdModel
import com.milk.funcall.common.ad.data.AdPositionModel
import com.milk.funcall.common.ad.data.AdResponseModel
import com.milk.funcall.common.ad.repo.AdRepository
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.net.json.JsonConvert
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger

object AdConfig {
    private const val AD_CONFIG = "AD_CONFIG"
    private val positionMap = mutableMapOf<String, String>()

    /** 获取网络中最新的广告信息 */
    fun switch() {
        if (positionMap.isNotEmpty()) return
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
                val config = getLocalConfig(BaseApplication.instance)
                JsonConvert.toModel(config, AdResponseModel::class.java)
                    ?.result?.let { updateAdUnitId(it) }
            }
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
            }
        }
        LiveEventBus.get<Any?>(EventKey.UPDATE_START_AD_UNIT_ID)
            .post(null)
    }

    /** 将广告 ID 保存在 Map 中 */
    private fun savePositionId(positionCode: String, positionList: MutableList<AdPositionModel>?) {
        if (positionList != null && positionList.size > 0) {
            positionMap[positionCode] = positionList[0].posId
        }
    }

    /** 网络获取广告失败、使用本地保存的广告 */
    private fun getLocalConfig(context: Context): String {
        val storedConfig = KvManger.getString(AD_CONFIG)
        return storedConfig.ifBlank {
            val defaultConfig = if (BuildConfig.DEBUG)
                String(context.resources.openRawResource(R.raw.config_debug).readBytes())
            else
                String(context.resources.openRawResource(R.raw.config_release).readBytes())
            KvManger.put(AD_CONFIG, defaultConfig)
            defaultConfig
        }
    }

    /** 获取广告 ID */
    fun getAdvertiseUnitId(key: String): String {
        val position = positionMap[key]
        return if (position?.isNotEmpty() == true) return position else ""
    }
}