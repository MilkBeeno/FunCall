package com.milk.funcall.common.paging

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StaggeredGridDecoration(
    context: Context,
    topSpace: Int,
    horizontalSpace: Int
) : RecyclerView.ItemDecoration() {

    private val top = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        topSpace.toFloat(),
        context.resources.displayMetrics
    )
    private val horizontal = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        horizontalSpace.toFloat(),
        context.resources.displayMetrics
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = top.toInt()
        outRect.left = horizontal.toInt()
        outRect.right = horizontal.toInt()
    }
}