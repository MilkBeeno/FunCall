package com.milk.funcall.user.ui.config

import com.milk.funcall.R
import com.milk.funcall.user.type.Gender

class GenderImage {
    fun obtain(gender: String): Int {
        return if (gender == Gender.Woman.value)
            R.drawable.create_name_gender_woman
        else
            R.drawable.create_name_gender_man
    }
}