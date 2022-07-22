package com.milk.funcall.common.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import com.milk.funcall.R

class SimplePopupWindow(
    private val context: Context,
    private val applyView: View,
    private val gravity: Int,
    private val offsetX: Int,
    private val offsetY: Int,
    private var putTopRequest: (() -> Unit)? = null,
    private var deleteRequest: (() -> Unit)? = null
) {
    private var popupWindow: PopupWindow? = null

    init {
        initializeView()
    }

    @SuppressLint("InflateParams")
    private fun initializeView() {
        val targetLayout = LayoutInflater.from(context)
            .inflate(R.layout.popup_conversation_to_top, null, false)
        popupWindow = PopupWindow(context)
        popupWindow?.contentView = targetLayout
        popupWindow?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow?.isOutsideTouchable = true
        popupWindow?.setBackgroundDrawable(null)
        // 设置当前位置信息
        popupWindow?.showAsDropDown(applyView, offsetX, offsetY, gravity)
        popupWindow?.setOnDismissListener {
            popupWindow?.contentView = null
            popupWindow = null
        }
        targetLayout.findViewById<AppCompatTextView>(R.id.tvPutTop)
            .setOnClickListener {
                popupWindow?.dismiss()
                putTopRequest?.invoke()
            }
        targetLayout.findViewById<AppCompatTextView>(R.id.tvDelete)
            .setOnClickListener {
                popupWindow?.dismiss()
                deleteRequest?.invoke()
            }
    }

    class Builder(private val context: Context) {
        private var applyView: View? = null
        private var gravity: Int = 0
        private var offsetX: Int = 0
        private var offsetY: Int = 0
        private var putTopRequest: (() -> Unit)? = null
        private var deleteRequest: (() -> Unit)? = null

        fun applyView(applyView: View) = apply {
            this.applyView = applyView
        }

        fun setGravity(gravity: Int) = apply {
            this.gravity = gravity
        }

        fun setOffsetX(offsetX: Int) = apply {
            this.offsetX = offsetX
        }

        fun setOffsetY(offsetY: Int) = apply {
            this.offsetY = offsetY
        }

        fun setPutTopRequest(putTopRequest: () -> Unit) = apply {
            this.putTopRequest = putTopRequest
        }

        fun setDeleteRequest(deleteRequest: () -> Unit) = apply {
            this.deleteRequest = deleteRequest
        }

        fun builder(): SimplePopupWindow {
            return SimplePopupWindow(
                context = context,
                applyView = checkNotNull(applyView),
                gravity = gravity,
                offsetX = offsetX,
                offsetY = offsetY,
                putTopRequest = putTopRequest,
                deleteRequest = deleteRequest
            )
        }
    }
}