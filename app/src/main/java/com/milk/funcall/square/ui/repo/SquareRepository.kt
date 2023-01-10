package com.milk.funcall.square.ui.repo

import com.milk.funcall.common.net.retrofit
import com.milk.funcall.square.api.ApiService

class SquareRepository {
    internal suspend fun getSquareInfo(gender: String) = retrofit {
        ApiService.squareApiService.getSquareInfo(gender)
    }
}