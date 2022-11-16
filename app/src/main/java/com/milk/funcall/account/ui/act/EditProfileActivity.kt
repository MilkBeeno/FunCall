package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
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
import com.milk.funcall.account.ui.vm.EditProfileViewModel
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.media.MediaLogger
import com.milk.funcall.common.media.engine.*
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.media.loader.VideoLoader
import com.milk.funcall.common.permission.Permission
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.NoScrollGridLayoutManager
import com.milk.funcall.common.ui.view.BanEnterInputFilter
import com.milk.funcall.databinding.ActivityEditProfileBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.ui.act.ImageMediaActivity
import com.milk.funcall.user.ui.act.VideoMediaActivity
import com.milk.funcall.user.ui.config.AvatarImage
import com.milk.simple.ktx.*

class EditProfileActivity : AbstractActivity() {
    private val binding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }
    private val editProfileViewModel by viewModels<EditProfileViewModel>()
    private val defaultAvatar by lazy { AvatarImage().obtain(Account.userGender) }
    private val imageAdapter by lazy { EditProfileImageAdapter() }
    private val uploadDialog by lazy { LoadingDialog(this, string(R.string.common_uploading)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeObserver()
        initializeView()
    }

    private fun initializeObserver() {
        FireBaseManager.logEvent(FirebaseKey.OPEN_EDIT_PAGE)
        LiveEventBus.get<String>(KvKey.EDIT_PROFILE_DELETE_VIDEO)
            .observe(this) { updateVideo("") }
        LiveEventBus.get<String>(KvKey.EDIT_PROFILE_DELETE_IMAGE)
            .observe(this) { updateImageList(removeImage = it) }
        Account.userAvatarFlow.collectLatest(this) { updateAvatar(it) }
        Account.userNameFlow.collectLatest(this) { binding.etName.setText(it) }
        Account.userBioFlow.collectLatest(this) { binding.etAboutMe.setText(it) }
        Account.userLinkFlow.collectLatest(this) { binding.etLink.setText(it) }
        Account.userVideoFlow.collectLatest(this) { updateVideo(it) }
        Account.userImageListFlow.collectLatest(this) { updateImageList(it) }
        editProfileViewModel.uploadResultFlow.collectLatest(this) {
            uploadDialog.dismiss()
            if (it) showToast(string(R.string.edit_profile_success))
        }
        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) FireBaseManager.logEvent(FirebaseKey.CLICK_ON_THE_NICKNAME_BOX)
        }
        binding.etLink.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) FireBaseManager.logEvent(FirebaseKey.CLICK_ON_THE_CONTACTION)
        }
        binding.etAboutMe.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) FireBaseManager.logEvent(FirebaseKey.CLICK_ON_PERSONAL_INTRODUCTION)
        }
    }

    private fun updateAvatar(avatar: String, localUpdate: Boolean = false) {
        when {
            avatar.isBlank() ->
                binding.ivUserAvatar.setImageResource(defaultAvatar)
            localUpdate ||
                editProfileViewModel.editAvatarPath.isBlank() -> {
                ImageLoader.Builder()
                    .loadAvatar(avatar)
                    .target(binding.ivUserAvatar)
                    .build()
            }
        }
    }

    private fun updateVideo(videoUrl: String, localUpdate: Boolean = false) {
        when {
            videoUrl.isBlank() -> {
                binding.ivVideoMask.gone()
                binding.ivVideo.setImageResource(R.drawable.common_media_add)
            }
            localUpdate ||
                editProfileViewModel.editVideoPath.isBlank() -> {
                binding.ivVideoMask.visible()
                VideoLoader.Builder()
                    .target(binding.ivVideo)
                    .placeholder(R.drawable.common_default_media_image)
                    .request(editProfileViewModel.editVideoPath)
                    .build()
            }
        }
    }

    private fun updateImageList(
        newImageList: MutableList<String> = arrayListOf(),
        appendImageList: MutableList<LocalMedia> = arrayListOf(),
        removeImage: String = ""
    ) {
        when {
            newImageList.isNotEmpty() -> {
                editProfileViewModel.editImageListPath.clear()
                newImageList.forEach { editProfileViewModel.editImageListPath.add(it) }
            }
            appendImageList.isNotEmpty() -> {
                appendImageList
                    .forEach { editProfileViewModel.editImageListPath.add(it.availablePath) }
            }
            removeImage.isNotBlank() -> {
                editProfileViewModel.editImageListPath.remove(removeImage)
            }
        }
        imageAdapter.setNewData(editProfileViewModel.editImageListPath)
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
        binding.etLink.filters = arrayOf(BanEnterInputFilter())
        // 选择图片显示
        binding.rvPicture.layoutManager = NoScrollGridLayoutManager(this, 3)
        binding.rvPicture.addItemDecoration(EditProfileImageGridDecoration(this))
        binding.rvPicture.adapter = imageAdapter
        imageAdapter.setItemOnClickListener { position, imageUrl ->
            if (imageUrl.isNotBlank()) {
                ImageMediaActivity.create(this)
                LiveEventBus.get<Pair<Int, MutableList<String>>>(KvKey.DISPLAY_IMAGE_MEDIA_LIST)
                    .post(Pair(position, editProfileViewModel.editImageListPath))
            } else {
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_UPLOAD_IMAGE_ICON)
                checkStoragePermission { toSelectImage() }
            }
        }
        binding.flEditAvatar.setOnClickListener(this)
        binding.flVideo.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.flEditAvatar -> checkStoragePermission {
                toSelectAvatarImage()
            }
            binding.flVideo -> checkStoragePermission {
                toSelectVideo()
            }
            binding.tvSave -> {
                val name = binding.etName.text.toString()
                val link = binding.etLink.text.toString()
                val bio = binding.etAboutMe.text.toString()
                if (name.isNotBlank()) {
                    uploadDialog.show()
                    editProfileViewModel.uploadProfile(name, bio, link)
                } else showToast(string(R.string.edit_profile_name_not_blank))
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
                    string(R.string.common_ok).uppercase(),
                    string(R.string.common_cancel).uppercase()
                )
            },
            resultRequest = { allGranted, _ ->
                if (allGranted) {
                    resultRequest()
                } else {
                    showToast(string(R.string.common_refuse))
                }
            })
    }

    private fun toSelectAvatarImage() {
        FireBaseManager.logEvent(FirebaseKey.CLICK_ON_AVATAR)
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
                    if (result != null && result.size > 0) {
                        val avatarPath = result[0].availablePath
                        editProfileViewModel.editAvatarPath = avatarPath
                        updateAvatar(avatarPath, true)
                        MediaLogger
                            .analyticalSelectResults(this@EditProfileActivity, result)
                    }
                }
            })
    }

    /** 选择视频资源 */
    private fun toSelectVideo() {
        if (editProfileViewModel.editVideoPath.isBlank()) {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .setLanguage(LanguageConfig.ENGLISH)
                .setSelectionMode(SelectModeConfig.SINGLE)
                .isCameraRotateImage(true)
                .isDirectReturnSingle(true)
                .setVideoPlayerEngine(IjkPlayerEngine.current)
                .setImageEngine(CoilVideoEngine.current)
                .isFilterSizeDuration(true)
                .setCompressEngine(ImageCompressEngine.current)
                .setSandboxFileEngine(SandboxFileEngine.current)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onCancel() = Unit
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (result != null && result.size > 0) {
                            val videoPath = result[0].availablePath
                            editProfileViewModel.editVideoPath = videoPath
                            updateVideo(videoPath, true)
                            MediaLogger
                                .analyticalSelectResults(this@EditProfileActivity, result)
                        }
                    }
                })
        } else VideoMediaActivity.create(this, editProfileViewModel.editVideoPath)
    }

    /** 最多可以选择六张图片 */
    private fun toSelectImage() {
        val num = 6 - editProfileViewModel.editImageListPath.size
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(CoilEngine.current)
            .setLanguage(LanguageConfig.ENGLISH)
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setMaxSelectNum(num)
            .isCameraRotateImage(true)
            .setCompressEngine(ImageCompressEngine.current)
            .setSandboxFileEngine(SandboxFileEngine.current)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onCancel() = Unit
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result != null && result.size > 0) {
                        updateImageList(appendImageList = result)
                        MediaLogger.analyticalSelectResults(this@EditProfileActivity, result)
                    }
                }
            })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    companion object {
        internal fun create(context: Context) {
            context.startActivity(Intent(context, EditProfileActivity::class.java))
        }
    }
}