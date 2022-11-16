package com.milk.funcall.account.repo

import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.account.api.AccountApiService
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit
import com.milk.simple.ktx.ioScope

object AccountRepository {
    private val accountApiService =
        ApiClient.getMainRetrofit().create(AccountApiService::class.java)

    internal fun getAccountInfo(registered: Boolean) {
        ioScope {
            val apiResponse = retrofit { accountApiService.getUserInfo() }
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                Account.saveAccountInfo(apiResult, registered)
                LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST).post(true)
            }
        }
    }
}