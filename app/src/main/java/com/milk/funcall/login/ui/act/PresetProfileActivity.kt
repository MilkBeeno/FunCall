package com.milk.funcall.login.ui.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.media.MediaLogger
import com.milk.funcall.common.media.engine.CoilEngine
import com.milk.funcall.common.media.engine.ImageCropEngine
import com.milk.funcall.common.media.engine.SandboxFileEngine
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.permission.Permission
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.view.BanEnterInputFilter
import com.milk.funcall.databinding.ActivityPresetProfileBinding
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.login.ui.vm.PresetProfileViewModel
import com.milk.funcall.user.ui.config.AvatarImage
import com.milk.funcall.user.ui.config.GenderImage
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.*

class PresetProfileActivity : AbstractActivity() {
    private val binding by lazy { ActivityPresetProfileBinding.inflate(layoutInflater) }
    private val presetProfileViewModel by viewModels<PresetProfileViewModel>()
    private val defaultGender by lazy { GenderImage().obtain(Account.userGender) }
    private val defaultAvatar by lazy { AvatarImage().obtain(Account.userGender) }
    private val uploadDialog by lazy { LoadingDialog(this, string(R.string.common_uploading)) }

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
        binding.headerToolbar.setTitle(string(R.string.preset_profile_title))
        binding.ivUserGender.setImageResource(defaultGender)
        binding.ivUserAvatar.setImageResource(defaultAvatar)
        binding.etUserName.filters =
            arrayOf(InputFilter.LengthFilter(20), BanEnterInputFilter())
        binding.ivUserAvatar.setOnClickListener(this)
        binding.tvStart.setOnClickListener(this)
        binding.etUserName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                FireBaseManager.logEvent(FirebaseKey.CHANGE_NAME)
            else
                KeyBoardUtil.hideKeyboard(this)
        }
    }

    private fun initializeObserver() {
        presetProfileViewModel.avatar.collectLatest(this) {
            if (it.isNotBlank()) ImageLoader.Builder()
                .loadAvatar(it)
                .target(binding.ivUserAvatar)
                .build()
        }
        presetProfileViewModel.name.collectLatest(this) {
            if (it.isNotBlank()) binding.etUserName.setText(it)
        }
        presetProfileViewModel.uploadImage.collectLatest(this) {
            if (it) {
                val name = binding.etUserName.text.toString()
                presetProfileViewModel.presetProfile(name)
            } else showToast(string(R.string.preset_profile_picture_upload_failed))
        }
        presetProfileViewModel.presetProfile.collectLatest(this) {
            uploadDialog.dismiss()
            if (it) {
                MainActivity.create(this)
                finish()
            } else showToast(string(R.string.preset_profile_profile_update_failed))
        }
    }

    private fun initializeData() {
        FireBaseManager
            .logEvent(FirebaseKey.OPEN_FILL_IN_THE_INFORMATION_PAGE)
        presetProfileViewModel.getUserAvatarName()
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivUserAvatar -> checkStoragePermission()
            binding.tvStart -> {
                when {
                    binding.etUserName.text.toString().trim().isBlank() -> {
                        showToast(string(R.string.preset_profile_name_empty))
                    }
                    else -> {
                        uploadDialog.show()
                        val name = binding.etUserName.text.toString()
                        presetProfileViewModel.updateUserProfile(name)
                    }
                }
            }
        }
    }

    private fun checkStoragePermission() {
        FireBaseManager.logEvent(FirebaseKey.CLICK_DEFAULT_AVATAR)
        Permission.checkStoragePermission(
            activity = this,
            refuseRequest = { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    string(R.string.common_media_permission_title),
                    string(R.string.common_ok).uppercase(),
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
            .setImageEngine(CoilEngine.current)
            .setLanguage(LanguageConfig.ENGLISH)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .isCameraRotateImage(true)
            .isDirectReturnSingle(true)
            .setCropEngine(ImageCropEngine.current)
            .setSandboxFileEngine(SandboxFileEngine.current)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onCancel() = Unit
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result != null) {
                        presetProfileViewModel.localAvatarPath = result[0].availablePath
                        ImageLoader.Builder()
                            .request(result[0].availablePath)
                            .target(binding.ivUserAvatar)
                            .build()
                        MediaLogger
                            .analyticalSelectResults(this@PresetProfileActivity, result)
                    }
                }
            })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        internal fun create(context: Context) =
            context.startActivity(Intent(context, PresetProfileActivity::class.java))
    }
}