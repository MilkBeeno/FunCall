package com.milk.funcall.square.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogSquareMatchingBinding

class MatchingDialog(activity: FragmentActivity) :
    SimpleDialog<DialogSquareMatchingBinding>(activity) {
    init {
        setWidthMatchParent(true)
        setHeightMatchParent(true)
        binding.squareMatch.setAnimation("square_match.json")
        binding.squareMatch.playAnimation()
    }

    internal fun setUserAvatars(avatars: MutableList<String>) {
        binding.squareLayout.setUserAvatars(avatars)
    }

    override fun getViewBinding(): DialogSquareMatchingBinding {
        return DialogSquareMatchingBinding.inflate(LayoutInflater.from(activity))
    }
}