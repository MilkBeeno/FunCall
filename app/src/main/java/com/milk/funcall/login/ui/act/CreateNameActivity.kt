package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.imageLoad.loadAvatar
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityCreateNameBinding
import com.milk.funcall.login.ui.vm.CreateNameViewModel
import com.milk.funcall.user.type.Gender
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.showToast
import com.milk.simple.ktx.string
import com.milk.simple.ktx.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreateNameActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityCreateNameBinding>()
    private val createNameViewModel by viewModels<CreateNameViewModel>()
    private val gender = Account.gender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar(binding.headerToolbar)
        createNameViewModel.obtainUserDefault(gender)
        initializeView()
        initializeObserver()
    }

    private fun initializeView() {
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(string(R.string.create_name_title))
        binding.ivUserAvatar.setOnClickListener(this)
        binding.tvCreateName.setOnClickListener(this)
        binding.etUserName.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.etUserName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) KeyBoardUtil.hideKeyboard(this)
        }
    }

    private fun initializeObserver() {
        lifecycleScope.launch {
            createNameViewModel.avatar.collectLatest {
                binding.ivUserAvatar.loadAvatar(
                    it, if (gender == Gender.Woman)
                        R.drawable.common_default_woman
                    else
                        R.drawable.common_default_man
                )
                binding.ivUserGender.setImageResource(
                    if (gender == Gender.Woman)
                        R.drawable.create_name_gender_woman
                    else
                        R.drawable.create_name_gender_man
                )
            }
            createNameViewModel.name.collectLatest {
                if (it.isNotBlank()) binding.etUserName.setText(it)
            }
        }
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

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, CreateNameActivity::class.java))
    }
}