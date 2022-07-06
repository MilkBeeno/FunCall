package com.milk.funcall.common.net.error

import android.app.Application

object ApiErrorHandler : ErrorHandler {
    private lateinit var appContext: Application

    override fun initialize(application: Application) {
        appContext = application
    }

    override fun post(code: Int, message: String?) {

    }
}