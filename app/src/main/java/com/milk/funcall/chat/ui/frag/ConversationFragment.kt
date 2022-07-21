package com.milk.funcall.chat.ui.frag

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.milk.funcall.R
import com.milk.funcall.chat.ui.adapter.ConversationAdapter
import com.milk.funcall.chat.ui.vm.ConversationViewModel
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentChatMessageBinding
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.visible

class ConversationFragment : AbstractFragment() {
    private val binding by lazy { FragmentChatMessageBinding.inflate(layoutInflater) }
    private val conversationViewModel by viewModels<ConversationViewModel>()
    private val conversationAdapter by lazy { ConversationAdapter() }

    override fun getRootView(): View = binding.root

    override fun initializeData() {
        conversationAdapter
            .setPagerSource(conversationViewModel.pagingSource.pager)
    }

    override fun initializeView() {
        binding.headerToolbar.setTitle(R.string.chat_message_title)
        binding.rvConversation.layoutManager = LinearLayoutManager(requireContext())
        binding.rvConversation.adapter = conversationAdapter
        conversationAdapter.addRefreshedListener {
            when (it) {
                RefreshStatus.Success -> {
                    binding.llEmpty.gone()
                }
                RefreshStatus.Empty -> {
                    binding.llEmpty.visible()
                }
                else -> Unit
            }
        }
    }

    companion object {
        fun create() = ConversationFragment()
    }
}