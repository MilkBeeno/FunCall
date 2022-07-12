package com.milk.funcall

import android.app.Application
import com.milk.funcall.common.author.FacebookAuth
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.media.loader.LoaderManager
import com.milk.funcall.common.net.error.ApiErrorHandler
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
        initializeLibrary()
    }

    private fun initializeLibrary() {
        ioScope {
            KvManger.initialize(instance)
            LoaderManager.initialize(instance)
            Logger.initialize(BuildConfig.DEBUG)
            ApiErrorHandler.initialize(instance)
            DataBaseManager.initialize(instance)
            FacebookAuth.initializeSdk(instance)
        }
    }
}