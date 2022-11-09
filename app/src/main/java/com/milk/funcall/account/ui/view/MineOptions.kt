package com.milk.funcall.account.ui.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.milk.funcall.R
import com.milk.funcall.databinding.LayoutMineOptionBinding
import com.milk.simple.ktx.color

class MineOptions : FrameLayout {
    private val binding =
        LayoutMineOptionBinding.inflate(LayoutInflater.from(context), this)

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    internal fun setOption(
        @DrawableRes image: Int,
        @StringRes string: Int,
        @ColorRes color: Int = context.color(R.color.FF1E1E21),
        showLine: Boolean = true
    ) {
        binding.ivContent.setImageResource(image)
        binding.vLine.visibility = if (showLine) VISIBLE else GONE
        binding.tvContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getString(string, context.theme)
        } else {
            context.resources.getString(string)
        }
        binding.tvContent.setTextColor(color)
    }
}