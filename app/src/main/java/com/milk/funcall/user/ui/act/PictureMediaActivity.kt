package com.milk.funcall.user.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.dialog.DeleteMediaDialog
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.HorizontalLinearLayoutManager
import com.milk.funcall.databinding.ActivityPictureMediaBinding
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.funcall.user.data.PictureMediaModel
import com.milk.funcall.user.ui.adapter.PictureMediaAdapter
import com.milk.funcall.user.ui.dialog.PictureMediaGuideDialog
import com.milk.simple.ktx.*

class PictureMediaActivity : AbstractActivity() {
    private val binding by lazy { ActivityPictureMediaBinding.inflate(layoutInflater) }
    private val guideDialog by lazy { PictureMediaGuideDialog(this) }
    private val pictureMediaModel by lazy {
        intent.getSerializableExtra(PICTURE_MEDIA_MODEL) as PictureMediaModel
    }
    private val imageMediaAdapter by lazy { PictureMediaAdapter() }
    private val pagerSnapHelper by lazy { PagerSnapHelper() }
    private val layoutManager by lazy { HorizontalLinearLayoutManager(this) }
    private val deleteDialog by lazy { DeleteMediaDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
    }

    private fun initializeView() {
        setStatusBarDark(false)
        setStatusBarColor(color(R.color.black))
        binding.headerToolbar.showArrowBack(R.drawable.common_cancle_white)
        binding.ivCancel.setOnClickListener(this)
        binding.llMessage.setOnClickListener(this)
        if (pictureMediaModel.targetId != Account.userId) {
            binding.ivCancel.gone()
            binding.llMessage.visible()
        } else {
            binding.ivCancel.visible()
            binding.llMessage.visibility = View.INVISIBLE
            deleteDialog.setOnConfirmListener {
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                val imageUrl = imageMediaAdapter.getItem(position)
                imageMediaAdapter.removeItem(position)
                binding.clIndicator.attachToRecyclerView(binding.rvImage, pagerSnapHelper)
                LiveEventBus.get<String>(KvKey.EDIT_PROFILE_DELETE_IMAGE).post(imageUrl)
                if (imageMediaAdapter.itemCount <= 0) finish()
            }
        }
        if (pictureMediaModel.pictureUrls.isNotEmpty()) {
            binding.rvImage.layoutManager = layoutManager
            binding.rvImage.adapter = imageMediaAdapter
            pagerSnapHelper.attachToRecyclerView(binding.rvImage)
            binding.rvImage.scrollToPosition(pictureMediaModel.position)
            imageMediaAdapter.setNewData(pictureMediaModel)
            binding.clIndicator.attachToRecyclerView(binding.rvImage, pagerSnapHelper)
            binding.clIndicator.changeIndicatorResource(
                R.drawable.shape_image_media_indicator_background_select,
                R.drawable.shape_image_media_indicator_background
            )
        }
    }

    private fun initializeObserver() {
        Account.userViewOtherFlow.collectLatest(this) {
            if (!it) {
                guideDialog.show()
                guideDialog.setOnDismissListener {
                    ioScope {
                        Account.userViewOther = true
                        Account.userViewOtherFlow.emit(true)
                    }
                }
            }
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivCancel -> {
                if (imageMediaAdapter.itemCount > 0) deleteDialog.show()
            }
            binding.llMessage -> {
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_MESSAGE_VIEW_IMAGE_PAGE)
                if (pictureMediaModel.isBlacked) return
                if (Account.userLogged)
                    ChatMessageActivity.create(this, pictureMediaModel.targetId)
                else {
                    showToast(string(R.string.common_place_to_login_first))
                    LoginActivity.create(this)
                }
            }
        }
    }

    companion object {
        private const val PICTURE_MEDIA_MODEL = "PICTURE_MEDIA_MODEL"
        internal fun create(context: Context, pictureMediaModel: PictureMediaModel) {
            val intent = Intent(context, PictureMediaActivity::class.java)
            intent.putExtra(PICTURE_MEDIA_MODEL, pictureMediaModel)
            context.startActivity(intent)
        }
    }
}