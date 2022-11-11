package com.milk.funcall.chat.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import com.milk.funcall.R
import com.milk.simple.ktx.string

class ChatMessagePopupWindow(
    private val context: Context,
    private val applyView: View,
    private val gravity: Int,
    private val offsetX: Int,
    private val offsetY: Int,
    private val isPutTopped: Boolean,
    private val isFollowed: Boolean,
    private var putTopRequest: (() -> Unit)? = null,
    private var followRequest: (() -> Unit)? = null,
    private var blackRequest: (() -> Unit)? = null,
    private var reportRequest: (() -> Unit)? = null
) {
    private var popupWindow: PopupWindow? = null

    init {
        initializeView()
    }

    @SuppressLint("InflateParams")
    private fun initializeView() {
        val targetLayout = LayoutInflater.from(context)
            .inflate(R.layout.popup_message, null, false)
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
        val tvPutTop = targetLayout.findViewById<AppCompatTextView>(R.id.tvPutTop)
        tvPutTop.text = if (isPutTopped) {
            context.string(R.string.common_un_pin)
        } else {
            context.string(R.string.common_up_top)
        }
        tvPutTop.setOnClickListener {
            popupWindow?.dismiss()
            putTopRequest?.invoke()
        }
        val tvFollow = targetLayout.findViewById<AppCompatTextView>(R.id.tvFollow)
        tvFollow.text = if (isFollowed) {
            context.string(R.string.common_un_follow)
        } else {
            context.string(R.string.common_follow)
        }
        tvFollow.setOnClickListener {
            popupWindow?.dismiss()
            followRequest?.invoke()
        }
        targetLayout.findViewById<AppCompatTextView>(R.id.tvBlack).setOnClickListener {
            popupWindow?.dismiss()
            blackRequest?.invoke()
        }
        targetLayout.findViewById<AppCompatTextView>(R.id.tvReport).setOnClickListener {
            popupWindow?.dismiss()
            reportRequest?.invoke()
        }
    }

    class Builder(private val context: Context) {
        private var applyView: View? = null
        private var gravity: Int = 0
        private var offsetX: Int = 0
        private var offsetY: Int = 0
        private var isPutTopped: Boolean = false
        private var isFollowed: Boolean = false
        private var putTopRequest: (() -> Unit)? = null
        private var followRequest: (() -> Unit)? = null
        private var blackRequest: (() -> Unit)? = null
        private var reportRequest: (() -> Unit)? = null

        internal fun applyView(applyView: View) = apply {
            this.applyView = applyView
        }

        internal fun setGravity(gravity: Int) = apply {
            this.gravity = gravity
        }

        internal fun setOffsetX(offsetX: Int) = apply {
            this.offsetX = offsetX
        }

        internal fun setOffsetY(offsetY: Int) = apply {
            this.offsetY = offsetY
        }

        internal fun setPutTopRequest(isPutTopped: Boolean, putTopRequest: () -> Unit) = apply {
            this.isPutTopped = isPutTopped
            this.putTopRequest = putTopRequest
        }

        internal fun setFollowRequest(isFollowed: Boolean, followRequest: () -> Unit) = apply {
            this.isFollowed = isFollowed
            this.followRequest = followRequest
        }

        internal fun setBlackRequest(blackRequest: () -> Unit) = apply {
            this.blackRequest = blackRequest
        }

        internal fun setReportRequest(reportRequest: () -> Unit) = apply {
            this.reportRequest = reportRequest
        }

        internal fun build(): ChatMessagePopupWindow {
            return ChatMessagePopupWindow(
                context = context,
                applyView = checkNotNull(applyView),
                gravity = gravity,
                offsetX = offsetX,
                offsetY = offsetY,
                isPutTopped = isPutTopped,
                isFollowed = isFollowed,
                putTopRequest = putTopRequest,
                followRequest = followRequest,
                blackRequest = blackRequest,
                reportRequest = reportRequest
            )
        }
    }
}