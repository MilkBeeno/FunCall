package com.milk.funcall.main

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow

object Account {
    internal val isLoggedState = MutableStateFlow(false)

    var userId = MutableLiveData<Long>(123)
    var userName = MutableLiveData<String>("")

    fun logout() {

    }
}