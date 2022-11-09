package com.milk.funcall.chat.ui.frag

import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.chat.ui.adapter.ConversationAdapter
import com.milk.funcall.chat.ui.dialog.ConversationPopupWindow
import com.milk.funcall.chat.ui.vm.ConversationViewModel
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentChatMessageBinding
import com.milk.simple.ktx.*

class ConversationFragment : AbstractFragment() {
    private val binding by lazy { FragmentChatMessageBinding.inflate(layoutInflater) }
    private val conversationViewModel by viewModels<ConversationViewModel>()
    private val conversationAdapter by lazy { ConversationAdapter() }
    private val splitHeight by lazy { requireActivity().obtainScreenHeight() / 2 }
    private val popupOffsetX by lazy { -requireContext().dp2px(190f).toInt() }
    private val popupOffsetY by lazy { requireActivity().dp2px(94.5f).toInt() }

    override fun getRootView(): View = binding.root

    override fun initializeView() {
        binding.headerToolbar.setTitle(R.string.chat_message_title)
        binding.rvConversation.layoutManager = LinearLayoutManager(requireContext())
        binding.rvConversation.adapter = conversationAdapter
        conversationAdapter.addRefreshedListener {
            when (it) {
                RefreshStatus.Success -> {
                    FireBaseManager.logEvent(FirebaseKey.ENTER_MESSAGE)
                    binding.llEmpty.gone()
                }
                RefreshStatus.Empty -> {
                    binding.llEmpty.visible()
                }
                else -> Unit
            }
        }
        conversationAdapter.setOnItemClickListener { adapter, _, position ->
            val targetId = adapter.getNoNullItem(position).conversation.targetId
            ChatMessageActivity.create(requireContext(), targetId)
        }
        conversationAdapter.setOnItemLongClickListener { adapter, itemView, position ->
            val conversation = adapter.getNoNullItem(position).conversation
            val isPutTopped = conversation.putTopTime > 0
            val local = intArrayOf(0, 0)
            itemView.getLocationInWindow(local)
            val offsetY =
                if (local[1] > splitHeight) popupOffsetY - itemView.measuredHeight else 0
            ConversationPopupWindow.Builder(requireActivity())
                .applyView(itemView)
                .setOffsetX(popupOffsetX)
                .setOffsetY(offsetY)
                .setGravity(Gravity.END)
                .setPutTopRequest(isPutTopped) {
                    if (isPutTopped) {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_THE_STICKY)
                        conversationViewModel.unPinChatMessage(conversation.targetId)
                    } else {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_TO_UNPIN)
                        conversationViewModel.putTopChatMessage(conversation.targetId)
                    }
                }
                .setDeleteRequest {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_THE_DELETE)
                    conversationViewModel.deleteChatMessage(conversation.targetId)
                }
                .build()
            true
        }
        binding.tvChatWithOther.setOnClickListener(this)
    }

    override fun initializeObserver() {
        super.initializeObserver()
        Account.userIdFlow.collectLatest(this) {
            conversationAdapter
                .setPagerSource(conversationViewModel.pagingSource.pager)
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        FireBaseManager.logEvent(FirebaseKey.CLICK_CHAT_WITH_OTHER)
        LiveEventBus.get<Any?>(EventKey.JUMP_TO_THE_HOME_PAGE).post(null)
    }

    companion object {
        internal fun create() = ConversationFragment()
    }
}