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
import com.milk.funcall.common.constrant.KvKey
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
import kotlinx.coroutines.flow.collectLatest

class EditProfileActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityEditProfileBinding>()
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
        LiveEventBus.get<String>(KvKey.EDIT_PROFILE_DELETE_VIDEO)
            .observe(this) { updateVideo("") }
        LiveEventBus.get<String>(KvKey.EDIT_PROFILE_DELETE_IMAGE)
            .observe(this) { updateImageList(removeImage = it) }
        launch { Account.userAvatarFlow.collectLatest { updateAvatar(it) } }
        launch { Account.userNameFlow.collectLatest { binding.etName.setText(it) } }
        launch { Account.userBioFlow.collectLatest { binding.etAboutMe.setText(it) } }
        launch { Account.userLinkFlow.collectLatest { binding.etLink.setText(it) } }
        launch { Account.userVideoFlow.collectLatest { updateVideo(it) } }
        launch { Account.userImageListFlow.collectLatest { updateImageList(it) } }
        launch {
            editProfileViewModel.uploadResult.collectLatest {
                uploadDialog.dismiss()
                if (it) showToast(string(R.string.edit_profile_success))
            }
        }
    }

    private fun updateAvatar(avatar: String) {
        editProfileViewModel.localAvatarPath = avatar
        if (avatar.isNotBlank()) {
            ImageLoader.Builder()
                .loadAvatar(avatar)
                .error(defaultAvatar)
                .target(binding.ivUserAvatar)
                .build()
        } else binding.ivUserAvatar.setImageResource(defaultAvatar)
    }

    private fun updateVideo(videoUrl: String) {
        editProfileViewModel.localVideoPath = videoUrl
        if (videoUrl.isNotBlank()) {
            binding.ivVideoMask.visible()
            VideoLoader.Builder()
                .target(binding.ivVideo)
                .placeholder(R.drawable.common_default_media_image)
                .request(editProfileViewModel.localVideoPath)
                .build()
        } else {
            binding.ivVideoMask.gone()
            binding.ivVideo.setImageResource(R.drawable.common_media_add)
        }
    }

    private fun updateImageList(
        newImageList: MutableList<String> = arrayListOf(),
        appendImageList: MutableList<LocalMedia> = arrayListOf(),
        removeImage: String = "",
    ) {
        when {
            newImageList.isNotEmpty() -> {
                editProfileViewModel.localImageListPath.clear()
                newImageList.forEach { editProfileViewModel.localImageListPath.add(it) }
            }
            appendImageList.isNotEmpty() -> {
                appendImageList
                    .forEach { editProfileViewModel.localImageListPath.add(it.availablePath) }
            }
            removeImage.isNotBlank() -> {
                editProfileViewModel.localImageListPath.remove(removeImage)
            }
        }
        imageAdapter.setNewData(editProfileViewModel.localImageListPath)
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
                uploadDialog.show()
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
                        updateAvatar(result[0].availablePath)
                        MediaLogger
                            .analyticalSelectResults(this@EditProfileActivity, result)
                    }
                }
            })
    }

    /** 选择视频资源 */
    private fun toSelectVideo() {
        if (editProfileViewModel.localVideoPath.isBlank()) {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .setLanguage(LanguageConfig.ENGLISH)
                .setSelectionMode(SelectModeConfig.SINGLE)
                .isCameraRotateImage(true)
                .isDirectReturnSingle(true)
                .setVideoPlayerEngine(IjkPlayerEngine.current)
                .setImageEngine(CoilVideoEngine.current)
                .setCompressEngine(ImageCompressEngine.current)
                .setSandboxFileEngine(SandboxFileEngine.current)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onCancel() = Unit
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (result != null && result.size > 0) {
                            updateVideo(result[0].availablePath)
                            MediaLogger
                                .analyticalSelectResults(this@EditProfileActivity, result)
                        }
                    }
                })
        } else VideoMediaActivity.create(this, editProfileViewModel.localVideoPath)
    }

    /** 最多可以选择六张图片 */
    private fun toSelectImage() {
        val num = 6 - editProfileViewModel.localImageListPath.size
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