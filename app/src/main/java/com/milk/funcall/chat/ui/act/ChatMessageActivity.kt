package com.milk.funcall.chat.ui.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.milk.funcall.R
import com.milk.funcall.chat.ui.adapter.ChatMessageAdapter
import com.milk.funcall.chat.ui.vm.ChatMessageViewModel
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityMessageBinding
import com.milk.simple.keyboard.KeyBoardUtil
import com.milk.simple.ktx.*

class ChatMessageActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityMessageBinding>()
    private val chatMessageViewModel by viewModels<ChatMessageViewModel>()
    private val chatMessageAdapter by lazy { ChatMessageAdapter() }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val targetName by lazy { intent.getStringExtra(TARGET_NAME).toString() }

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
        chatMessageViewModel.updateTargetUser(targetId)
        chatMessageAdapter.setPagerSource(chatMessageViewModel.pagingSource.pager)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
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

    companion object {
        private const val TARGET_ID = "TARGET_ID"
        private const val TARGET_NAME = "TARGET_NAME"
        fun create(context: Context, targetId: Long, targetName: String) {
            val intent = Intent(context, ChatMessageActivity::class.java)
            intent.putExtra(TARGET_ID, targetId)
            intent.putExtra(TARGET_NAME, targetName)
            context.startActivity(intent)
        }
    }
}