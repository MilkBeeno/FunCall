package com.milk.funcall.chat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.milk.simple.keyboard.KeyBoardUtil

class ChatRecyclerView : RecyclerView {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            KeyBoardUtil.hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }
}