package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel

class EditProfileViewModel : ViewModel() {
    var localAvatarPath: String = ""
    val localImageListPath = mutableListOf<String>()
}