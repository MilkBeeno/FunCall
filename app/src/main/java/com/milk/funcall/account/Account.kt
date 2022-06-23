package com.milk.funcall.account

import androidx.lifecycle.MutableLiveData

object Account {
    var userId = MutableLiveData<Long>(123)
    var userName = MutableLiveData<String>("")
}