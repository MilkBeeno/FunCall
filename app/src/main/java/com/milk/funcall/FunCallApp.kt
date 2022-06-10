package com.milk.funcall

import android.app.Application
import android.content.Context
import android.util.Log
import com.milk.simple.log.Logger

class FunCallApp : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initializeLibrary()
    }

    private fun initializeLibrary() {
        Logger.initialize(BuildConfig.DEBUG || Log.isLoggable("MyLog", Log.DEBUG))
    }
}