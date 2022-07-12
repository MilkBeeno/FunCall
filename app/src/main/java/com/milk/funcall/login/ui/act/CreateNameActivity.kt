package com.milk.funcall.login.ui.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.media.engine.GlideEngine
import com.milk.funcall.common.media.engine.ImageCropEngine
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.permission.Permission
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityCreateNameBinding
import com.milk.funcall.login.ui.vm.CreateNameViewModel
import com.milk.funcall.user.type.Gender
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.*
import java.io.File

class CreateNameActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityCreateNameBinding>()
    private val createNameViewModel by viewModels<CreateNameViewModel>()
    private val isMale by lazy { Account.userGender == Gender.Man.value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeObserver()
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.root.navigationBarPadding()
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
            binding.ivUserAvatar -> checkStoragePermission()
            binding.tvCreateName -> {
                if (binding.etUserName.text.toString().trim().isBlank())
                    showToast(string(R.string.create_name_no_empty))
                else
                    MainActivity.create(this)
            }
        }
    }

    private fun checkStoragePermission() {
        Permission.checkStoragePermission(
            activity = this,
            refuseRequest = { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    string(R.string.common_media_permission_title),
                    string(R.string.common_confirm).uppercase(),
                    string(R.string.common_cancel).uppercase()
                )
            },
            resultRequest = { allGranted, _ ->
                if (allGranted)
                    toSelectAvatarImage()
                else
                    showToast(string(R.string.common_refuse))
            })
    }

    @SuppressLint("CheckResult")
    private fun toSelectAvatarImage() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setLanguage(LanguageConfig.ENGLISH)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .isDirectReturnSingle(true)
            .setCropEngine(ImageCropEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onCancel() = Unit
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result != null) {
                        Glide.with(this@CreateNameActivity)
                            .load(File(result[0].path))
                            .into(binding.ivUserAvatar)
                    }
                }
            })
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