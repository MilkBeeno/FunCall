package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.asLiveData
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.adapter.EditProfileImageAdapter
import com.milk.funcall.account.ui.decoration.EditProfileImageGridDecoration
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.media.MediaLogger
import com.milk.funcall.common.media.engine.CoilEngine
import com.milk.funcall.common.media.engine.ImageCompressEngine
import com.milk.funcall.common.media.engine.ImageCropEngine
import com.milk.funcall.common.media.engine.SandboxFileEngine
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.permission.Permission
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.NoScrollGridLayoutManager
import com.milk.funcall.common.ui.view.BanEnterInputFilter
import com.milk.funcall.databinding.ActivityEditProfileBinding
import com.milk.funcall.user.ui.act.ImageMediaActivity
import com.milk.funcall.user.ui.config.AvatarImage
import com.milk.funcall.user.ui.vm.EditProfileViewModel
import com.milk.simple.ktx.*

class EditProfileActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityEditProfileBinding>()
    private val editProfileViewModel by viewModels<EditProfileViewModel>()
    private val defaultAvatar by lazy { AvatarImage().obtain(Account.userGender) }
    private val imageAdapter by lazy { EditProfileImageAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeObserver()
        initializeView()
    }

    private fun initializeObserver() {
        Account.userAvatarFlow.asLiveData().observe(this) {
            if (it.isNotBlank()) {
                ImageLoader.Builder()
                    .loadAvatar(it)
                    .error(defaultAvatar)
                    .target(binding.ivUserAvatar)
                    .build()
            } else binding.ivUserAvatar.setImageResource(defaultAvatar)
        }
        Account.userNameFlow.asLiveData().observe(this) {
            if (it.isNotBlank()) binding.etName.setText(it)
        }
        Account.userBioFlow.asLiveData().observe(this) {
            if (it.isNotBlank()) binding.etAboutMe.setText(it)
        }
        Account.userImageListFlow.asLiveData().observe(this) { images ->
            editProfileViewModel.localImageListPath.clear()
            images.forEach { editProfileViewModel.localImageListPath.add(it) }
            imageAdapter.setNewData(editProfileViewModel.localImageListPath)
        }
        LiveEventBus.get<String>(KvKey.EDIT_PROFILE_DELETE_IMAGE).observe(this) {
            editProfileViewModel.localImageListPath.remove(it)
            imageAdapter.setNewData(editProfileViewModel.localImageListPath)
        }
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.root.navigationBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(string(R.string.edit_profile_title))
        binding.etName.filters =
            arrayOf(InputFilter.LengthFilter(20), BanEnterInputFilter())
        binding.etAboutMe.filters =
            arrayOf(InputFilter.LengthFilter(150), BanEnterInputFilter())
        val currentLength = binding.etAboutMe.text?.length ?: 0
        binding.tvCount.text = currentLength.toString().plus("/150")
        binding.etAboutMe.addTextChangedListener {
            val changeLength = it?.length ?: 0
            binding.tvCount.text = changeLength.toString().plus("/150")
        }
        // 选择图片显示
        binding.rvPicture.layoutManager = NoScrollGridLayoutManager(this, 3)
        binding.rvPicture.addItemDecoration(EditProfileImageGridDecoration(this))
        binding.rvPicture.adapter = imageAdapter
        imageAdapter.setItemOnClickListener { position, imageUrl ->
            if (imageUrl.isNotBlank()) {
                ImageMediaActivity.create(this)
                LiveEventBus.get<Pair<Int, MutableList<String>>>(KvKey.DISPLAY_IMAGE_MEDIA_LIST)
                    .post(Pair(position, editProfileViewModel.localImageListPath))
            } else checkStoragePermission { toSelectImage() }
        }
        binding.flEditAvatar.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.flEditAvatar -> checkStoragePermission {
                toSelectAvatarImage()
            }
            binding.tvSave -> {
                val name = binding.etName.text.toString()
                val bio = binding.etAboutMe.text.toString()
                val link = binding.etLink.text.toString()
                editProfileViewModel.uploadProfile(name, bio, link)
            }
        }
    }

    private fun checkStoragePermission(resultRequest: () -> Unit) {
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
                if (allGranted) resultRequest() else showToast(string(R.string.common_refuse))
            })
    }

    private fun toSelectAvatarImage() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(CoilEngine())
            .setLanguage(LanguageConfig.ENGLISH)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .isCameraRotateImage(true)
            .isDirectReturnSingle(true)
            .setCropEngine(ImageCropEngine())
            .setSandboxFileEngine(SandboxFileEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onCancel() = Unit
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result != null) {
                        editProfileViewModel.localAvatarPath = result[0].availablePath
                        ImageLoader.Builder()
                            .request(result[0].availablePath)
                            .target(binding.ivUserAvatar)
                            .build()
                        MediaLogger
                            .analyticalSelectResults(this@EditProfileActivity, result)
                    }
                }
            })
    }

    /** 最多可以选择六张图片 */
    private fun toSelectImage() {
        val num = 6 - editProfileViewModel.localImageListPath.size
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(CoilEngine())
            .setLanguage(LanguageConfig.ENGLISH)
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setMaxSelectNum(num)
            .isCameraRotateImage(true)
            .setCompressEngine(ImageCompressEngine())
            .setSandboxFileEngine(SandboxFileEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onCancel() = Unit
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result != null) {
                        result.forEach {
                            editProfileViewModel
                                .localImageListPath.add(it.availablePath)
                        }
                        imageAdapter.setNewData(editProfileViewModel.localImageListPath)
                        MediaLogger
                            .analyticalSelectResults(this@EditProfileActivity, result)
                    }
                }
            })
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