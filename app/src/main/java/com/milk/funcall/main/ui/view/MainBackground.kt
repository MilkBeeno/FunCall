package com.milk.funcall.main.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.milk.funcall.R

class MainBackground : AppCompatImageView {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    fun setFullBackground() {
        setImageResource(R.drawable.main_background_full)
        scaleType = ScaleType.FIT_XY
    }

    fun setMediumBackground() {
        setImageResource(R.drawable.main_background_medium)
        scaleType = ScaleType.FIT_START
    }
}