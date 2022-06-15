package com.milk.funcall

import android.app.Application
import android.util.Log
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.LogTree
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
            initializeLogger()
            KvManger.initialize(instance)
            DataBaseManager.initialize(instance)
        }
    }

    private fun initializeLogger() {
        val debug = BuildConfig.DEBUG || Log.isLoggable("MyLog", Log.DEBUG)
        Logger.initialize(debug, tree = object : LogTree {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                // 将错误日历写在文件中
            }
        })
    }
}