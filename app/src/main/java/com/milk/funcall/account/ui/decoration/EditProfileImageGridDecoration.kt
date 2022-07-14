package com.milk.funcall.account.ui.decoration

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EditProfileImageGridDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val topSpace =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        )
    private val horizontalSpace =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
        )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = topSpace.toInt()
        when (parent.getChildAdapterPosition(view) % 3) {
            0 -> {
                outRect.left = 0
                outRect.right = horizontalSpace.toInt()
            }
            1 -> {
                outRect.left = horizontalSpace.toInt()
                outRect.right = horizontalSpace.toInt()
            }
            2 -> {
                outRect.left = horizontalSpace.toInt()
                outRect.right = 0
            }
        }
    }
}