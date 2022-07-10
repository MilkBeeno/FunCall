package com.milk.funcall.chat.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.milk.funcall.R
import com.milk.funcall.chat.ui.adapter.ChatMessageAdapter
import com.milk.funcall.chat.vm.MessageViewModel
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityMessageBinding
import com.milk.simple.ktx.*

class ChatMessageActivity : AbstractActivity() {

    private val binding by viewBinding<ActivityMessageBinding>()
    private val messageViewModel by viewModels<MessageViewModel>()
    private val chatMessageAdapter by lazy { ChatMessageAdapter() }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val targetName by lazy { intent.getStringExtra(TARGET_NAME).toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        setStatusBarDark()
        setStatusBarColor(color(R.color.white))
        binding.root.navigationBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(targetName)
        binding.rvMessage.adapter = chatMessageAdapter
        binding.rvMessage.layoutManager = LinearLayoutManager(this)
        chatMessageAdapter.addRefreshedListener {
            when (it) {
                RefreshStatus.Success -> {
                    binding.tvEmpty.gone()
                }
                RefreshStatus.Empty -> {
                    binding.tvEmpty.visible()
                }
                else -> {
                    // 其他状态目前不需要
                }
            }
        }
    }

    private fun initializeData() {
        messageViewModel.updateTargetUser(targetId)
        chatMessageAdapter.setPagerSource(messageViewModel.pagingSource.pager)
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