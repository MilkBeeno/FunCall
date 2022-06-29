package com.milk.funcall.main.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import com.milk.funcall.R
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityEditProfileBinding
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.string
import com.milk.simple.ktx.viewBinding

class EditProfileActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityEditProfileBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar(binding.headerToolbar)
        initializeView()
    }

    private fun initializeView() {
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(string(R.string.edit_profile_title))
        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) KeyBoardUtil.hideKeyboard(this)
        }
        binding.etAboutMe.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) KeyBoardUtil.hideKeyboard(this)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, EditProfileActivity::class.java))
        }
    }
}