package com.milk.funcall.user.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityImageMediaBinding
import com.milk.funcall.user.data.UserMediaModel
import com.milk.funcall.user.ui.adapter.ImageMediaAdapter
import com.milk.funcall.user.ui.dialog.ImageMediaGuideDialog
import com.milk.simple.ktx.*

class ImageMediaActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityImageMediaBinding>()
    private val guideDialog by lazy { ImageMediaGuideDialog(this) }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val targetName by lazy { intent.getStringExtra(TARGET_NAME).toString() }
    private val isBlacked by lazy { intent.getBooleanExtra(IS_BLACKED, false) }

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
        binding.tvMessage.setOnClickListener(this)
    }

    private fun initializeObserver() {
        Account.userViewOtherFlow.asLiveData().observe(this) {
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
        LiveEventBus.get<Pair<Int, MutableList<UserMediaModel>>>(KvKey.DISPLAY_IMAGE_MEDIA_LIST)
            .observeSticky(this) {
                binding.rvImage.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.rvImage.adapter = ImageMediaAdapter(it.second)
                val pagerSnapHelper = PagerSnapHelper()
                pagerSnapHelper.attachToRecyclerView(binding.rvImage)
                binding.clIndicator.attachToRecyclerView(binding.rvImage, pagerSnapHelper)
                binding.clIndicator.changeIndicatorResource(
                    R.drawable.shape_image_media_indicator_background_select,
                    R.drawable.shape_image_media_indicator_background
                )
                binding.rvImage.scrollToPosition(it.first)
            }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.tvMessage -> {
                if (isBlacked) return
                ChatMessageActivity.create(this, targetId, targetName)
            }
        }
    }

    companion object {
        private const val IS_BLACKED = "IS_BLACKED"
        private const val TARGET_ID = "TARGET_ID"
        private const val TARGET_NAME = "TARGET_NAME"
        fun create(context: Context, targetId: Long, targetName: String, isBlacked: Boolean) {
            val intent = Intent(context, ImageMediaActivity::class.java)
            intent.putExtra(TARGET_ID, targetId)
            intent.putExtra(TARGET_NAME, targetName)
            intent.putExtra(IS_BLACKED, isBlacked)
            context.startActivity(intent)
        }
    }
}