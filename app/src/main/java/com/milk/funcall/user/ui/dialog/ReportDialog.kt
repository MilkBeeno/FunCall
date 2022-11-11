package com.milk.funcall.user.ui.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.R
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogReportBinding
import com.milk.funcall.user.status.ReportType

class ReportDialog(activity: FragmentActivity) : SimpleDialog<DialogReportBinding>(activity) {
    private var reportRequest: ((ReportType) -> Unit)? = null

    init {
        setGravity(Gravity.BOTTOM)
        setWidthMatchParent(true)
        setCanceledOnTouchOutside(true)
        setWindowAnimations(R.style.BottomDialog_Animation)
        binding.tvFirst.setOnClickListener {
            dismiss()
            reportRequest?.invoke(ReportType.Harassment)
        }
        binding.tvSecond.setOnClickListener {
            dismiss()
            reportRequest?.invoke(ReportType.Bilk)
        }
        binding.tvThird.setOnClickListener {
            dismiss()
            reportRequest?.invoke(ReportType.Nudity)
        }
    }

    internal fun setReportListener(listener: (ReportType) -> Unit) {
        reportRequest = listener
    }

    override fun getViewBinding(): DialogReportBinding {
        return DialogReportBinding.inflate(LayoutInflater.from(activity))
    }
}