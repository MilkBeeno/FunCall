package com.milk.funcall.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.StringRes
import com.milk.funcall.databinding.ViewToolbarBinding

class CustomToolbar : FrameLayout {

    private val binding = ViewToolbarBinding
        .inflate(LayoutInflater.from(context), this, true)

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    fun setTitle(@StringRes resId: Int) {
        binding.tvTitle.text = context.resources.getString(resId)
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun clickArrowBack(
        action: () -> Unit = {
            if (context is Activity) (context as Activity).onBackPressed()
        }
    ) {
        binding.ivBack.visibility = VISIBLE
        binding.ivBack.setOnClickListener { action() }
    }
}