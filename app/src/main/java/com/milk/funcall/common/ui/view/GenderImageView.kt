package com.milk.funcall.common.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.milk.funcall.R
import com.milk.funcall.user.status.Gender

class GenderImageView : AppCompatImageView {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    fun updateGender(gender: String) {
        visibility = when (gender) {
            Gender.Man.value -> {
                setBackgroundResource(R.drawable.common_gender_man)
                VISIBLE
            }
            Gender.Woman.value -> {
                setBackgroundResource(R.drawable.common_gender_woman)
                VISIBLE
            }
            else -> GONE
        }
    }
}