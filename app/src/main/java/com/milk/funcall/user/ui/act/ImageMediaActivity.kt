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
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.HorizontalLinearLayoutManager
import com.milk.funcall.databinding.ActivityImageMediaBinding
import com.milk.funcall.user.ui.adapter.ImageMediaAdapter
import com.milk.funcall.user.ui.dialog.ImageMediaGuideDialog
import com.milk.simple.ktx.*
import kotlinx.coroutines.flow.collectLatest

class ImageMediaActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityImageMediaBinding>()
    private val guideDialog by lazy { ImageMediaGuideDialog(this) }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val targetName by lazy { intent.getStringExtra(TARGET_NAME).toString() }
    private val targetAvatar by lazy { intent.getStringExtra(TARGET_AVATAR).toString() }
    private val isBlacked by lazy { intent.getBooleanExtra(IS_BLACKED, false) }
    private val imageMediaAdapter by lazy { ImageMediaAdapter() }
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
        if (targetId > 0) {
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
        binding.ivCancel.setOnClickListener(this)
        binding.llMessage.setOnClickListener(this)
    }

    private fun initializeObserver() {
        launch {
            Account.userViewOtherFlow.collectLatest {
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
        LiveEventBus.get<Pair<Int, MutableList<String>>>(KvKey.DISPLAY_IMAGE_MEDIA_LIST)
            .observeSticky(this) {
                if (it.second.isEmpty()) return@observeSticky
                binding.rvImage.layoutManager = layoutManager
                binding.rvImage.adapter = imageMediaAdapter
                pagerSnapHelper.attachToRecyclerView(binding.rvImage)
                imageMediaAdapter.setNewData(it.second)
                binding.rvImage.scrollToPosition(it.first)
                binding.clIndicator.attachToRecyclerView(binding.rvImage, pagerSnapHelper)
                binding.clIndicator.changeIndicatorResource(
                    R.drawable.shape_image_media_indicator_background_select,
                    R.drawable.shape_image_media_indicator_background
                )
            }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivCancel -> {
                if (imageMediaAdapter.itemCount > 0) deleteDialog.show()
            }
            binding.llMessage -> {
                if (isBlacked) return
                ChatMessageActivity
                    .create(this, targetId, targetName, targetAvatar)
            }
        }
    }

    companion object {
        private const val TARGET_ID = "TARGET_ID"
        private const val TARGET_NAME = "TARGET_NAME"
        private const val TARGET_AVATAR = "TARGET_AVATAR"
        private const val IS_BLACKED = "IS_BLACKED"
        fun create(
            context: Context,
            targetId: Long = 0,
            targetName: String = "",
            targetAvatar: String = "",
            isBlacked: Boolean = false
        ) {
            val intent = Intent(context, ImageMediaActivity::class.java)
            intent.putExtra(TARGET_ID, targetId)
            intent.putExtra(TARGET_NAME, targetName)
            intent.putExtra(TARGET_AVATAR, targetAvatar)
            intent.putExtra(IS_BLACKED, isBlacked)
            context.startActivity(intent)
        }
    }
}