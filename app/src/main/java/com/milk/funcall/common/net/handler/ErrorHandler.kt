package com.milk.funcall.common.net.handler

import android.app.Application

interface ErrorHandler {
    fun initialize(application: Application)
    fun post(code: Int, message: String?)
}