package com.milk.funcall.login.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val status = MutableLiveData<Boolean>()
}