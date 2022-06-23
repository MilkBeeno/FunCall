package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.milk.funcall.R
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.Gender
import com.milk.funcall.databinding.ActivityCreateNameBinding
import com.milk.funcall.main.ui.act.MainActivity
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.showToast
import com.milk.simple.ktx.string
import com.milk.simple.mdr.KvManger

class CreateNameActivity : AbstractActivity() {
    private val binding by lazy { ActivityCreateNameBinding.inflate(layoutInflater) }
    private val gender by lazy { KvManger.getInt(KvKey.USER_GENDER) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar(binding.headerToolbar)
        initializeView()
    }

    private fun initializeView() {
        binding.headerToolbar.clickArrowBack()
        binding.headerToolbar.setTitle(string(R.string.create_name_title))
        val isFemale = gender == Gender.Woman.value
        binding.ivUserAvatar.setImageResource(
            if (isFemale)
                R.drawable.create_name_default_woman
            else
                R.drawable.create_name_default_man
        )
        binding.ivUserGender.setImageResource(
            if (isFemale)
                R.drawable.create_name_gender_woman
            else
                R.drawable.create_name_gender_man
        )
        binding.ivUserAvatar.setOnClickListener(this)
        binding.tvCreateName.setOnClickListener(this)
        binding.ivUserUpdate.setOnClickListener(this)
        binding.etUserName.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.etUserName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) KeyBoardUtil.hideKeyboard(this)
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivUserAvatar -> {

            }
            binding.ivUserUpdate -> {

            }
            binding.tvCreateName -> {
                if (binding.etUserName.text.toString().isBlank())
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