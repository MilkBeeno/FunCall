package com.milk.funcall.common.net.handler

import android.app.Application

object ApiErrorHandler : ErrorHandler {
    const val retrofitCatchCode = Int.MAX_VALUE
    private lateinit var appContext: Application

    override fun initialize(application: Application) {
        appContext = application
    }

    override fun post(code: Int, message: String?) {

    }
}