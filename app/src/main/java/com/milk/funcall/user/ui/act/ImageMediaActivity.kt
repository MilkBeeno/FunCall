package com.milk.funcall.user.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityImageMediaBinding
import com.milk.funcall.user.data.UserMediaModel
import com.milk.funcall.user.ui.adapter.ImageMediaAdapter
import com.milk.funcall.user.ui.dialog.ImageMediaGuideDialog
import com.milk.simple.ktx.*

class ImageMediaActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityImageMediaBinding>()
    private val position by lazy { intent.getIntExtra(IMAGE_POSITION, 0) }
    private val guideDialog by lazy { ImageMediaGuideDialog(this) }

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
        LiveEventBus.get<MutableList<UserMediaModel>>(KvKey.DISPLAY_IMAGE_MEDIA_LIST)
            .observeSticky(this) {
                binding.rvImage.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.rvImage.adapter = ImageMediaAdapter(it)
                val pagerSnapHelper = PagerSnapHelper()
                pagerSnapHelper.attachToRecyclerView(binding.rvImage)
                binding.clIndicator.attachToRecyclerView(binding.rvImage, pagerSnapHelper)
                binding.clIndicator.changeIndicatorResource(
                    R.drawable.shape_image_media_indicator_background_select,
                    R.drawable.shape_image_media_indicator_background
                )
                binding.rvImage.scrollToPosition(position)
            }
    }

    companion object {
        private const val IMAGE_POSITION = "IMAGE_POSITION"
        fun create(context: Context, position: Int) =
            context.startActivity(Intent(context, ImageMediaActivity::class.java)
                .apply { putExtra(IMAGE_POSITION, position) })
    }
}