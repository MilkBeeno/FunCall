package com.milk.funcall.chat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.milk.funcall.R
import com.milk.funcall.databinding.LayoutMessageRedDotBinding

class MessageRedDotView : FrameLayout {

    private val binding = LayoutMessageRedDotBinding
        .inflate(LayoutInflater.from(context), this, true)

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    internal fun updateMessageCount(count: Int, maxCount: Int = 99) {
        when {
            count in 1..maxCount -> {
                val params = layoutParams as ViewGroup.LayoutParams
                params.width = params.height
                layoutParams = params
                binding.redDotRootView.text = count.toString()
                setBackgroundResource(R.drawable.shape_message_red_dot_small)
                visibility = VISIBLE
            }
            count > maxCount -> {
                val params = layoutParams as ViewGroup.LayoutParams
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams = params
                val childParams = binding.redDotRootView.layoutParams as LayoutParams
                childParams.leftMargin = dp2px(6f)
                childParams.rightMargin = dp2px(5f)
                binding.redDotRootView.layoutParams = childParams
                binding.redDotRootView.text = maxCount.toString().plus("+")
                setBackgroundResource(R.drawable.shape_message_red_dot_medium)
                visibility = VISIBLE
            }
            else -> visibility = GONE
        }
    }

    private fun dp2px(dpValue: Float): Int =
        (dpValue * (context.resources.displayMetrics.density) + 0.5f).toInt()
}