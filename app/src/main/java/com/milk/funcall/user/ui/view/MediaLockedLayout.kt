package com.milk.funcall.user.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.milk.funcall.R

class MediaLockedLayout : FrameLayout {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    private var clickRequest: (() -> Unit)? = null

    init {
        val rootView = LayoutInflater.from(context)
            .inflate(R.layout.layout_media_locked, this, true)
        rootView.findViewById<LinearLayoutCompat>(R.id.llViewNow)
            .setOnClickListener { clickRequest?.invoke() }
    }

    fun setOnClickListener(clickListener: () -> Unit) {
        this.clickRequest = clickListener
    }
}