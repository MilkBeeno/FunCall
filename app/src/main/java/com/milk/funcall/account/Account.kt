package com.milk.funcall.account

import androidx.lifecycle.MutableLiveData
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.user.type.Gender
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import kotlinx.coroutines.flow.MutableStateFlow

object Account {
    /** 获取当前用户的性别或更新用户性别 */
    internal var gender: Gender = Gender.Man
        set(value) {
            KvManger.put(KvKey.USER_GENDER, value.value)
            field = value
        }
        get() {
            val value = KvManger.getString(KvKey.USER_GENDER)
            field = if (value == Gender.Woman.value) Gender.Woman else Gender.Man
            return field
        }

    /** 获取本地保存当前用户的登录状态或更新登录状态 */
    internal var isLogged: Boolean = false
        set(value) {
            KvManger.put(KvKey.USER_LOGGED_STATE, value)
            field = value
        }
        get() {
            field = KvManger.getBoolean(KvKey.USER_LOGGED_STATE)
            return field
        }

    /** 登录状态事件流 */
    internal val isLoggedState = MutableStateFlow(false)

    /** 获取本地保存当前用户的登录 Token 或更新登录 Token */
    internal var accessToken: String = ""
        set(value) {
            KvManger.put(KvKey.USER_ACCESS_TOKEN, value)
            field = value
        }
        get() {
            field = KvManger.getString(KvKey.USER_ACCESS_TOKEN)
            return field
        }

    var userId = MutableLiveData<Long>(123)
    var userName = MutableLiveData<String>("")

    internal fun initialize() {
        ioScope {
            isLoggedState.emit(isLogged)
        }
    }

    internal fun logged(token: String) {
        ioScope {
            isLogged = false
            accessToken = token
            isLoggedState.emit(true)
        }
    }

    internal fun logout() {
        ioScope {
            gender = Gender.Man
            isLogged = false
            accessToken = ""
            isLoggedState.emit(false)
        }
    }

    fun saveUserInfo() {

    }
}