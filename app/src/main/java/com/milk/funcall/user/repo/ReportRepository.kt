package com.milk.funcall.user.repo

import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.user.api.ReportApiService
import com.milk.funcall.user.status.ReportType

object ReportRepository {
    private val reportApiService =
        ApiClient.getMainRetrofit().create(ReportApiService::class.java)

    internal suspend fun report(userId: Long, type: ReportType) = retrofit {
        reportApiService.report(userId, type.value)
    }
}