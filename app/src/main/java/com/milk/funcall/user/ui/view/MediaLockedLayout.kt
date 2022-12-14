package com.milk.funcall.user.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.milk.funcall.R
import com.milk.funcall.app.AppConfig
import com.milk.funcall.databinding.LayoutMediaLockedBinding
import com.milk.simple.ktx.color
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.string
import com.milk.simple.ktx.visible

class MediaLockedLayout : FrameLayout {
    private val binding = LayoutMediaLockedBinding
        .inflate(LayoutInflater.from(context), this, true)

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    private var clickRequest: (() -> Unit)? = null

    init {
        binding.llViewNow.setOnClickListener { clickRequest?.invoke() }
    }

    internal fun setOnClickRequest(clickRequest: () -> Unit) {
        this.clickRequest = clickRequest
    }

    internal fun setBackgroundDrawable() {
        binding.root.setBackgroundResource(
            R.drawable.shape_user_info_media_locked_background
        )
    }

    internal fun setBackgroundColor() {
        binding.root.setBackgroundColor(context.color(R.color.white))
    }

    internal fun setMediaTimes(unlockMethod: Int, remainUnlockCount: Int) {
        val maxTimes = if (unlockMethod == 1) {
            binding.ivMediaType
                .setImageResource(R.drawable.user_info_media_locked_view)
            AppConfig.freeUnlockTimes
        } else {
            binding.ivMediaType
                .setImageResource(R.drawable.user_info_media_locked_view_ad)
            AppConfig.viewAdUnlockTimes
        }
        if (maxTimes < 10) {
            binding.tvMediaTimes.visible()
            binding.tvMediaTimes.text =
                "(".plus(context.string(R.string.user_info_unlock_times))
                    .plus(" ")
                    .plus(remainUnlockCount)
                    .plus("/")
                    .plus(maxTimes)
                    .plus(")")
        } else binding.tvMediaTimes.gone()
    }
}