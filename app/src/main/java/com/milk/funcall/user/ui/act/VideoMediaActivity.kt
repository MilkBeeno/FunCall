package com.milk.funcall.user.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.dialog.DeleteMediaDialog
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.media.view.IjkVideoView
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityVideoMediaBinding
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.simple.ktx.*
import tv.danmaku.ijk.media.player.IMediaPlayer

class VideoMediaActivity : AbstractActivity() {
    private val binding by lazy { ActivityVideoMediaBinding.inflate(layoutInflater) }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val isBlacked by lazy { intent.getBooleanExtra(IS_BLACKED, false) }
    private val videoUrl by lazy { intent.getStringExtra(VIDEO_URL).toString() }
    private val deleteDialog by lazy { DeleteMediaDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
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
                LiveEventBus.get<String>(KvKey.EDIT_PROFILE_DELETE_VIDEO).post(videoUrl)
                finish()
            }
        }
        binding.videoView.setVideoPath(videoUrl)
        binding.ivCancel.setOnClickListener(this)
        binding.ivVideoStart.setOnClickListener(this)
        binding.llMessage.setOnClickListener(this)
        binding.videoView.setListener(object : IjkVideoView.VideoPlayerListener() {
            override fun onCompletion(p0: IMediaPlayer?) {
                super.onCompletion(p0)
                binding.ivVideoStart.visible()
            }
        })
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivCancel -> {
                deleteDialog.show()
            }
            binding.ivVideoStart -> {
                binding.videoView.start()
                binding.ivVideoStart.gone()
            }
            binding.llMessage -> {
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_MESSAGE_VIEW_VIDEO_PAGE)
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

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding.videoView.pause()
    }

    companion object {
        private const val VIDEO_URL = "VIDEO_URL"
        private const val TARGET_ID = "TARGET_ID"
        private const val IS_BLACKED = "IS_BLACKED"
        internal fun create(
            context: Context,
            videoUrl: String,
            targetId: Long = 0,
            isBlacked: Boolean = false
        ) {
            val intent = Intent(context, VideoMediaActivity::class.java)
            intent.putExtra(VIDEO_URL, videoUrl)
            intent.putExtra(TARGET_ID, targetId)
            intent.putExtra(IS_BLACKED, isBlacked)
            context.startActivity(intent)
        }
    }
}