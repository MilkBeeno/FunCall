package com.milk.funcall.common.ui.dialog

import android.app.Dialog
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

abstract class AbstractDialog<T : ViewBinding>(protected val activity: FragmentActivity) {
    private var dialog: Dialog? = null
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

    private fun createDialog() {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                setContentView(binding.root)
                window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setOnCancelListener { dismissRequest?.invoke() }
                setOnDismissListener { dismissRequest?.invoke() }
                initialize()
            }
        }
    }

    abstract fun getViewBinding(): T
    abstract fun initialize()

    open fun show() {
        createDialog()
        dialog?.show()
    }

    open fun dismiss() {
        dialog?.dismiss()
    }

    open fun setDimAmount(amount: Float) {
        dialog?.window?.setDimAmount(amount)
    }

    open fun setCanceledOnTouchOutside(cancel: Boolean = true) {
        dialog?.setCanceledOnTouchOutside(cancel)
    }
}