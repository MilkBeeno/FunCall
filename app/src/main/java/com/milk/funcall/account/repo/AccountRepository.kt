package com.milk.funcall.account.repo

import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.account.api.ApiService
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.net.retrofit
import com.milk.simple.ktx.ioScope

object AccountRepository {
    fun getAccountInfo(registered: Boolean) {
        ioScope {
            val apiResponse = retrofit { ApiService.accountApiService.getUserInfo() }
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                Account.saveAccountInfo(apiResult, registered)
                LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST)
                    .post(true)
            }
        }
    }
}