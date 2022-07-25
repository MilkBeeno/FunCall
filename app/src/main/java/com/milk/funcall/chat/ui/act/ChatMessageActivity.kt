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
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityMessageBinding
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.*
import kotlinx.coroutines.flow.collectLatest

class ChatMessageActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityMessageBinding>()
    private val chatMessageViewModel by viewModels<ChatMessageViewModel>()
    private val chatMessageAdapter by lazy { ChatMessageAdapter() }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val targetName by lazy { intent.getStringExtra(TARGET_NAME).toString() }
    private val targetAvatar by lazy { intent.getStringExtra(TARGET_AVATAR).toString() }

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
        binding.headerToolbar.setTitle(targetName)
        binding.rvMessage.adapter = chatMessageAdapter
        binding.rvMessage.layoutManager = LinearLayoutManager(this)
        binding.etMessage.addTextChangedListener { updateSendState() }
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
    }

    private fun updateSendState() {
        if (binding.etMessage.text.toString().isBlank()) {
            binding.tvSend.isClickable = false
            binding.tvSend.setBackgroundResource(R.drawable.shape_chat_message_send_un_available)
        } else {
            binding.tvSend.isClickable = true
            binding.tvSend.setBackgroundResource(R.drawable.shape_chat_message_send_available)
        }
    }

    private fun initializeData() {
        launch {
            chatMessageViewModel.getTargetInfoByDB(targetId)
                .collectLatest {
                    if (it != null) {
                        binding.headerToolbar.setTitle(it.targetName)
                        chatMessageViewModel
                            .updateTargetUser(it.targetId, it.targetName, it.targetAvatar)
                        chatMessageAdapter.setUserInfoEntity(it)
                    } else chatMessageViewModel
                        .updateTargetUser(targetId, targetName, targetAvatar)
                    chatMessageAdapter
                        .setPagerSource(chatMessageViewModel.pagingSource.pager)
                }
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
                chatMessageViewModel.sendTextChatMessage(messageContent)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun showPopupWindow() {
        ChatMessagePopupWindow.Builder(this)
            .applyView(binding.ivMore)
            .setOffsetX(-dp2px(150f).toInt())
            .setOffsetY(dp2px(10f).toInt())
            .setGravity(Gravity.END)
            .setPutTopRequest(false) {
                //  if (false)
                //conversationViewModel.unPinChatMessage(conversation.targetId)
                // else
                //.putTopChatMessage(conversation.targetId)
            }
            .setFollowRequest(false) {

            }
            .setBlackRequest {
                // 直接拉黑
            }
            .builder()
    }

    override fun onPause() {
        super.onPause()
        chatMessageViewModel.updateUnReadCount()
    }

    companion object {
        private const val TARGET_ID = "TARGET_ID"
        private const val TARGET_NAME = "TARGET_NAME"
        private const val TARGET_AVATAR = "TARGET_AVATAR"
        fun create(
            context: Context,
            targetId: Long,
            targetName: String = "",
            targetAvatar: String = ""
        ) {
            val intent = Intent(context, ChatMessageActivity::class.java)
            intent.putExtra(TARGET_ID, targetId)
            intent.putExtra(TARGET_NAME, targetName)
            intent.putExtra(TARGET_AVATAR, targetAvatar)
            context.startActivity(intent)
        }
    }
}