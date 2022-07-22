package com.milk.funcall.account.ui.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.milk.simple.ktx.dp2px

class EditProfileImageGridDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val topSpace = context.dp2px(10f).toInt()
    private val horizontalSpace = context.dp2px(3f).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = topSpace
        when (parent.getChildAdapterPosition(view) % 3) {
            0 -> {
                outRect.left = 0
                outRect.right = horizontalSpace
            }
            1 -> {
                outRect.left = horizontalSpace
                outRect.right = horizontalSpace
            }
            2 -> {
                outRect.left = horizontalSpace
                outRect.right = 0
            }
        }
    }
}