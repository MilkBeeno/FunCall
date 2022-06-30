package com.milk.funcall.common.ui.dialog

import android.app.Dialog
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

abstract class AbstractDialog<T : ViewBinding>(val activity: FragmentActivity) {
    private var dialog: Dialog? = null
    private var dimAmount: Float = 0f
    private var cancelable: Boolean = true
    private var canceledOnTouchOutside: Boolean = true
    private var dismissRequest: (() -> Unit)? = null
    protected val binding: T by lazy { getViewBinding() }

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                dialog = null
            }
        })
    }

    abstract fun getViewBinding(): T

    private fun createDialog() {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                setContentView(binding.root)
                window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setCancelable(cancelable)
                setCanceledOnTouchOutside(canceledOnTouchOutside)
                setOnCancelListener { dismissRequest?.invoke() }
                setOnDismissListener { dismissRequest?.invoke() }
                // 解决头设置透明度问题、需要放置这里
                dialog?.window?.setDimAmount(dimAmount)
            }
        }
    }

    open fun show() {
        createDialog()
        dialog?.show()
    }

    open fun dismiss() {
        dialog?.dismiss()
    }

    open fun setDimAmount(amount: Float) {
        dimAmount = amount
    }

    open fun setCanceledOnTouchOutside(cancel: Boolean) {
        canceledOnTouchOutside = cancel
    }

    open fun setCancelable(flag: Boolean) {
        cancelable = flag
    }
}