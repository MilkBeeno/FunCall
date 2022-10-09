package com.milk.funcall.common.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.milk.funcall.R
import com.milk.funcall.databinding.LayoutToolbarBinding
import com.milk.simple.ktx.color

class HeaderToolbar : FrameLayout {

    private val binding = LayoutToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    fun setTitle(@StringRes resId: Int, @ColorRes color: Int = context.color(R.color.FF1E1E21)) {
        binding.tvTitle.text = context.resources.getString(resId)
        binding.tvTitle.setTextColor(color)
    }

    fun setTitle(title: String, @ColorRes color: Int = context.color(R.color.FF1E1E21)) {
        binding.tvTitle.text = title
        binding.tvTitle.setTextColor(color)
    }

    fun showArrowBack(
        @DrawableRes drawableResId: Int = R.drawable.common_arrow_back, action: () -> Unit = {
            if (context is Activity) (context as Activity).onBackPressed()
        }
    ) {
        binding.ivBack.setImageResource(drawableResId)
        binding.ivBack.visibility = VISIBLE
        binding.ivBack.setOnClickListener { action() }
    }
}