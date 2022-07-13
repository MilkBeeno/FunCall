package com.milk.funcall.login.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.login.repo.CreateNameRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class CreateNameViewModel : ViewModel() {
    private val createNameRepository by lazy { CreateNameRepository() }
    val avatar = MutableSharedFlow<String>()
    val name = MutableSharedFlow<String>()

    fun getUserAvatarName() {
        ioScope {
            val apiResponse =
                createNameRepository.getUserAvatarName(Account.userGender)
            val apiResult = apiResponse.data
            avatar.emit(apiResult?.avatarUrl ?: "")
            name.emit(apiResult?.nickname ?: "")
        }
    }
}