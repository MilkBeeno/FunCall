package com.milk.funcall.main

import androidx.lifecycle.MutableLiveData
import com.milk.funcall.common.constrant.KvKey
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import kotlinx.coroutines.flow.MutableStateFlow

object Account {
    internal val isLogged = MutableStateFlow(false)
    internal var accessToken = MutableStateFlow("")

    var userId = MutableLiveData<Long>(123)
    var userName = MutableLiveData<String>("")

    internal fun initialize() {
        ioScope {
            val loggedState = KvManger.getBoolean(KvKey.USER_LOGGED_STATE)
            if (loggedState) {
                isLogged.emit(true)
                accessToken.emit(KvManger.getString(KvKey.USER_ACCESS_TOKEN))
            } else isLogged.emit(false)
        }
    }

    internal fun logged(token: String) {
        ioScope {
            isLogged.emit(true)
            accessToken.emit(token)
            KvManger.put(KvKey.USER_LOGGED_STATE, true)
            KvManger.put(KvKey.USER_ACCESS_TOKEN, token)
        }
    }

    internal fun logout() {
        ioScope {
            isLogged.emit(false)
            accessToken.emit("")
            KvManger.put(KvKey.USER_LOGGED_STATE, false)
            KvManger.put(KvKey.USER_ACCESS_TOKEN, "")
        }
    }

    fun saveUserInfo() {

    }
}