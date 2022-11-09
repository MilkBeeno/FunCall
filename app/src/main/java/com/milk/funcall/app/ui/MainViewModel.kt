package com.milk.funcall.app.ui

import androidx.lifecycle.ViewModel
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.author.DeviceManager
import com.milk.funcall.login.repo.LoginRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class MainViewModel : ViewModel() {
    internal val logOutFlow = MutableSharedFlow<Boolean>()

    internal fun uploadDeviceInfo() {
        val deviceId = DeviceManager.number
        val loginRepository = LoginRepository()
        ioScope {
            val apiResponse = loginRepository.login(deviceId, AuthType.NULL, deviceId)
            logOutFlow.emit(apiResponse.success)
        }
    }

    internal fun getConversationCount() = MessageRepository.getConversationCount()
}