package com.milk.funcall.user.ui.config

import com.milk.funcall.R
import com.milk.funcall.user.type.Gender

class AvatarImage {
    fun obtain(gender: String): Int {
        return if (gender == Gender.Woman.value)
            R.drawable.common_default_woman
        else
            R.drawable.common_default_man
    }
}