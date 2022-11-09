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
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.funcall.user.ui.adapter.ImageMediaAdapter
import com.milk.funcall.user.ui.dialog.ImageMediaGuideDialog
import com.milk.simple.ktx.*

class ImageMediaActivity : AbstractActivity() {
    private val binding by lazy { ActivityImageMediaBinding.inflate(layoutInflater) }
    private val guideDialog by lazy { ImageMediaGuideDialog(this) }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
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
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_MESSAGE_VIEW_IMAGE_PAGE)
                if (isBlacked) return
                if (Account.userLogged)
                    ChatMessageActivity.create(this, targetId)
                else {
                    showToast(string(R.string.common_place_to_login_first))
                    LoginActivity.create(this)
                }
            }
        }
    }

    companion object {
        private const val TARGET_ID = "TARGET_ID"
        private const val IS_BLACKED = "IS_BLACKED"
        internal fun create(context: Context, targetId: Long = 0, isBlacked: Boolean = false) {
            val intent = Intent(context, ImageMediaActivity::class.java)
            intent.putExtra(TARGET_ID, targetId)
            intent.putExtra(IS_BLACKED, isBlacked)
            context.startActivity(intent)
        }
    }
}