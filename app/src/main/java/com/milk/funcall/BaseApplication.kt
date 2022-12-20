package com.milk.funcall

import android.app.Application
import com.milk.funcall.app.AppConfig
import com.milk.funcall.app.AppRepository
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.author.DeviceManager
import com.milk.funcall.common.author.FacebookAuth
import com.milk.funcall.common.db.DataBaseManager
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.media.loader.LoaderConfig
import com.milk.funcall.common.net.error.ApiErrorHandler
import com.milk.funcall.common.pay.PayManager
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger

class BaseApplication : Application() {
    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        getAppConfig()
        initializeLibrary()
    }

    private fun initializeLibrary() {
        ioScope {
            KvManger.initialize(instance)
            DeviceManager.initialize(instance)
            LoaderConfig.initialize(instance)
            Logger.initialize(BuildConfig.DEBUG)
            ApiErrorHandler.initialize(instance)
            DataBaseManager.initialize(instance)
            FacebookAuth.initializeSdk(instance)
            AdManager.initialize(instance)
            FireBaseManager.initialize(instance)
        }
    }

    private fun getAppConfig() {
        ioScope {
            val apiResponse = AppRepository().getConfig(
                BuildConfig.AD_APP_ID,
                BuildConfig.AD_APP_VERSION,
                BuildConfig.AD_APP_CHANNEL
            )
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                AppConfig.save(apiResult)
                PayManager.googlePay.addProduct(AppConfig.subsYearId)
                PayManager.googlePay.addProduct(AppConfig.subsWeekId)
                PayManager.googlePay.addProduct(AppConfig.subsYearDiscountId)
                PayManager.googlePay.initialize(instance)
            }
        }
    }
}