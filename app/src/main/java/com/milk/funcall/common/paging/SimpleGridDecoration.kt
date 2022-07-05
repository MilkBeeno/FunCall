package com.milk.funcall.common.paging

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SimpleGridDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val ten =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        )
    private val four =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics
        )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = ten.toInt()
        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            outRect.left = ten.toInt()
            outRect.right = four.toInt()
        } else {
            outRect.left = four.toInt()
            outRect.right = ten.toInt()
        }
    }
}