package com.milk.funcall

import android.app.Application
import android.util.Log
import com.milk.funcall.mdr.DataBaseManager
import com.milk.simple.log.Logger

class BaseApplication : Application() {
    companion object {
        lateinit var INSTANCE: Application
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initializeLibrary()
    }

    private fun initializeLibrary() {
        Logger.initialize(BuildConfig.DEBUG || Log.isLoggable("MyLog", Log.DEBUG))
        DataBaseManager.initializeDataBase(INSTANCE)
    }
}