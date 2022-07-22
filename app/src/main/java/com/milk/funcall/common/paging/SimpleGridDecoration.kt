package com.milk.funcall.common.paging

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.milk.simple.ktx.dp2px

class SimpleGridDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val ten = context.dp2px(10f).toInt()
    private val four = context.dp2px(4f).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = ten
        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            outRect.left = ten
            outRect.right = four
        } else {
            outRect.left = four
            outRect.right = ten
        }
    }
}