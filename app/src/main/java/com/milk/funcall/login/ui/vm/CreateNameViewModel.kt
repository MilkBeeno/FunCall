package com.milk.funcall.login.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.login.repo.CreateNameRepository
import com.milk.funcall.user.type.Gender
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableStateFlow

class CreateNameViewModel : ViewModel() {
    private val createNameRepository by lazy { CreateNameRepository() }
    val avatar = MutableStateFlow("")
    val name = MutableStateFlow("")

    fun obtainUserDefault(gender: Gender) {
        ioScope {
            val apiResponse =
                createNameRepository.obtainUserDefault(gender)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                avatar.emit(apiResult.avatarUrl)
                name.emit(apiResult.nickname)
            }
        }
    }
}