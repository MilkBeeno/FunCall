package com.milk.funcall.user.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogPictureMediaBinding

class PictureMediaGuideDialog(activity: FragmentActivity) :
    SimpleDialog<DialogPictureMediaBinding>(activity) {
    override fun getViewBinding(): DialogPictureMediaBinding {
        return DialogPictureMediaBinding.inflate(LayoutInflater.from(activity))
    }
}