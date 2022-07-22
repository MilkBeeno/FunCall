package com.milk.funcall.common.paging

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.milk.simple.ktx.dp2px

class StaggeredGridDecoration(
    context: Context,
    topSpace: Int,
    horizontalSpace: Int
) : RecyclerView.ItemDecoration() {
    private val top = context.dp2px(topSpace.toFloat()).toInt()
    private val horizontal = context.dp2px(horizontalSpace.toFloat()).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = top
        outRect.left = horizontal
        outRect.right = horizontal
    }
}