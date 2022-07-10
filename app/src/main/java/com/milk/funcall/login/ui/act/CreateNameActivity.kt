package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.media.ImageLoader
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityCreateNameBinding
import com.milk.funcall.login.ui.vm.CreateNameViewModel
import com.milk.funcall.user.type.Gender
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.showToast
import com.milk.simple.ktx.string
import com.milk.simple.ktx.viewBinding

class CreateNameActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityCreateNameBinding>()
    private val createNameViewModel by viewModels<CreateNameViewModel>()
    private val isMale by lazy { Account.userGender == Gender.Man.value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar(binding.headerToolbar)
        initializeObserver()
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        binding.headerToolbar.setTitle(string(R.string.create_name_title))
        val defaultGender =
            if (isMale) R.drawable.create_name_gender_woman else R.drawable.create_name_gender_man
        binding.ivUserGender.setImageResource(defaultGender)
        binding.etUserName.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.etUserName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) KeyBoardUtil.hideKeyboard(this)
        }
        binding.ivUserAvatar.setOnClickListener(this)
        binding.tvCreateName.setOnClickListener(this)
    }

    private fun initializeObserver() {
        createNameViewModel.avatar.asLiveData().observe(this) {
            if (it.isNotBlank())
                ImageLoader.Builder()
                    .loadAvatar(it, isMale)
                    .target(binding.ivUserAvatar)
                    .build()
            else {
                val defaultAvatar =
                    if (isMale) R.drawable.common_default_woman else R.drawable.common_default_man
                binding.ivUserAvatar.setImageResource(defaultAvatar)
            }
        }
        createNameViewModel.name.asLiveData().observe(this) {
            if (it.isNotBlank()) binding.etUserName.setText(it)
        }
    }

    private fun initializeData() {
        createNameViewModel.getUserAvatarName()
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivUserAvatar -> {
                // 系统去选择头像
            }
            binding.tvCreateName -> {
                if (binding.etUserName.text.toString().trim().isBlank())
                    showToast(string(R.string.create_name_no_empty))
                else
                    MainActivity.create(this)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, CreateNameActivity::class.java))
    }
}