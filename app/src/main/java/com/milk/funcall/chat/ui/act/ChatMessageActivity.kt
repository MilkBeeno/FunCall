package com.milk.funcall.chat.ui.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.milk.funcall.R
import com.milk.funcall.chat.ui.adapter.ChatMessageAdapter
import com.milk.funcall.chat.ui.dialog.ChatMessagePopupWindow
import com.milk.funcall.chat.ui.vm.ChatMessageViewModel
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityMessageBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.funcall.user.ui.dialog.ReportDialog
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.*

class ChatMessageActivity : AbstractActivity() {
    private val binding by lazy { ActivityMessageBinding.inflate(layoutInflater) }
    private val chatMessageViewModel by viewModels<ChatMessageViewModel>()
    private val chatMessageAdapter by lazy { ChatMessageAdapter() }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private val reportDialog by lazy { ReportDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeView() {
        setStatusBarDark()
        setStatusBarColor(color(R.color.white))
        binding.headerToolbar.showArrowBack()
        binding.rvMessage.adapter = chatMessageAdapter
        binding.rvMessage.layoutManager = LinearLayoutManager(this)
        binding.etMessage.addTextChangedListener { updateSendState() }
        binding.rvMessage.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (oldBottom != -1 && oldBottom > bottom && chatMessageAdapter.itemCount > 0) {
                binding.rvMessage.requestLayout()
                binding.rvMessage.post {
                    binding.rvMessage.scrollToPosition(chatMessageAdapter.itemCount - 1)
                }
            }
        }
        // 滑动内容收起键盘逻辑、还需要修改
        binding.rvMessage.setOnTouchListener { _, _ ->
            binding.etMessage.clearFocus()
            false
        }
        // 监听内容变化
        chatMessageAdapter.addRefreshedListener {
            when (it) {
                RefreshStatus.Success -> {
                    val position = chatMessageAdapter.itemCount - 1
                    binding.rvMessage.scrollToPosition(position)
                    binding.clSayHi.gone()
                }
                RefreshStatus.Empty -> {
                    binding.clSayHi.visible()
                }
                else -> Unit
            }
        }
        chatMessageAdapter.setOnItemChildClickListener { adapter, _, position ->
            val targetId = adapter.getNoNullItem(position).targetId
            UserInfoActivity.create(this, targetId)
        }
        // 监听输入内容键盘焦点变化
        binding.etMessage.setOnFocusChangeListener { _, hasFocus ->
            val itemCount = chatMessageAdapter.itemCount
            if (hasFocus && itemCount > 0) {
                val position = chatMessageAdapter.itemCount - 1
                binding.rvMessage.smoothScrollToPosition(position)
            }
            if (!hasFocus) KeyBoardUtil.hideKeyboard(this)
        }
        binding.tvSend.setOnClickListener(this)
        binding.ivSayHiCancel.setOnClickListener(this)
        binding.tvSayHiSend.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        reportDialog.setReportListener {
            loadingDialog.show()
            chatMessageViewModel.report(targetId, it)
        }
    }

    private fun updateSendState() {
        if (binding.etMessage.text.toString().isBlank()) {
            binding.tvSend.setBackgroundResource(R.drawable.shape_chat_message_send_un_available)
        } else {
            binding.tvSend.setBackgroundResource(R.drawable.shape_chat_message_send_available)
        }
    }

    private fun initializeData() {
        chatMessageViewModel.getTargetInfoByDB(targetId).collectLatest(this) {
            if (it != null) {
                binding.headerToolbar.setTitle(it.targetName)
                chatMessageViewModel.updateUserInfoEntity(it)
                chatMessageAdapter.setUserInfoEntity(it)
                chatMessageAdapter.setPagerSource(chatMessageViewModel.pagingSource.pager)
            } else chatMessageViewModel.getTargetInfoByNetwork(targetId)
        }
        chatMessageViewModel.followedStatusFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it == true) showToast(string(R.string.common_success))
        }
        chatMessageViewModel.blackUserFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it) finish()
        }
        chatMessageViewModel.reportFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it) showLongToast(string(R.string.common_report_successful))
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivMore -> showPopupWindow()
            binding.ivSayHiCancel -> binding.clSayHi.gone()
            binding.tvSayHiSend -> {
                val messageContent = binding.tvSayHiTitle.text.toString()
                chatMessageViewModel.sendTextChatMessage(messageContent)
            }
            binding.tvSend -> {
                val messageContent = binding.etMessage.text.toString()
                if (messageContent.isNotBlank()) {
                    chatMessageViewModel.sendTextChatMessage(messageContent)
                }
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun showPopupWindow() {
        val isPutTopped = chatMessageViewModel.userPutTopStatus
        val isFollowed = chatMessageViewModel.userInfoEntity?.targetIsFollowed ?: false
        ChatMessagePopupWindow.Builder(this)
            .applyView(binding.ivMore)
            .setOffsetX(-dp2px(150f).toInt())
            .setOffsetY(dp2px(10f).toInt())
            .setGravity(Gravity.END)
            .setPutTopRequest(isPutTopped) {
                if (isPutTopped) {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_TOP_ON_CHAT_PAGE)
                    chatMessageViewModel.unPinChatMessage()
                } else {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_UNPIN__ON_CHAT_PAGE)
                    chatMessageViewModel.putTopChatMessage()
                }
            }
            .setFollowRequest(isFollowed) {
                if (!isFollowed) {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_FOLLOW_ON_CHAT_PAGE)
                }
                loadingDialog.show()
                chatMessageViewModel.changeFollowedStatus()
            }
            .setBlackRequest {
                FireBaseManager.logEvent(FirebaseKey.CLICK_BLACKOUT_ON_CHAT_PAGE)
                loadingDialog.show()
                chatMessageViewModel.blackUser()
            }
            .setReportRequest {
                reportDialog.show()
            }
            .build()
    }

    override fun onPause() {
        super.onPause()
        chatMessageViewModel.updateUnReadCount()
    }

    companion object {
        private const val TARGET_ID = "TARGET_ID"
        internal fun create(context: Context, targetId: Long) {
            val intent = Intent(context, ChatMessageActivity::class.java)
            intent.putExtra(TARGET_ID, targetId)
            context.startActivity(intent)
        }
    }
}