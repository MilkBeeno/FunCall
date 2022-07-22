package com.milk.funcall.chat.ui.frag

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.milk.funcall.R
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.chat.ui.adapter.ConversationAdapter
import com.milk.funcall.chat.ui.vm.ConversationViewModel
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.common.ui.dialog.SimplePopupWindow
import com.milk.funcall.databinding.FragmentChatMessageBinding
import com.milk.funcall.user.ui.act.UserTotalInfoActivity
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.obtainScreenHeight
import com.milk.simple.ktx.visible

class ConversationFragment : AbstractFragment() {
    private val binding by lazy { FragmentChatMessageBinding.inflate(layoutInflater) }
    private val conversationViewModel by viewModels<ConversationViewModel>()
    private val conversationAdapter by lazy { ConversationAdapter() }
    private val splitHeight by lazy { requireActivity().obtainScreenHeight() / 2 }

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
        conversationAdapter.setOnItemClickListener { adapter, _, position ->
            val targetId = adapter.getNoNullItem(position).conversation.targetId
            val targetName = conversationAdapter.getTargetName(adapter.getNoNullItem(position))
            val targetAvatar = conversationAdapter.getTargetAvatar(adapter.getNoNullItem(position))
            ChatMessageActivity.create(requireContext(), targetId, targetName, targetAvatar)
        }
        conversationAdapter.setOnItemLongClickListener { adapter, itemView, position ->
            val targetId = adapter.getNoNullItem(position).conversation.targetId
            val local = intArrayOf(0, 0)
            itemView.getLocationInWindow(local)
            val offsetY =
                if (local[1] > splitHeight) -dpToPx(94.5f) - itemView.measuredHeight else 0
            SimplePopupWindow.Builder(requireActivity())
                .applyView(itemView)
                .setOffsetX(-dpToPx(200f))
                .setOffsetY(offsetY)
                .setGravity(Gravity.END)
                .setPutTopRequest {
                    conversationViewModel.putTopChatMessage(targetId)
                }
                .setDeleteRequest {
                    conversationViewModel.deleteChatMessage(targetId)
                }
                .builder()
            true
        }
        conversationAdapter.setOnItemChildClickListener { adapter, _, position ->
            val targetId = adapter.getNoNullItem(position).conversation.targetId
            UserTotalInfoActivity.create(requireContext(), targetId)
        }
    }

    private fun dpToPx(value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            requireActivity().resources.displayMetrics
        ).toInt()
    }

    companion object {
        fun create() = ConversationFragment()
    }
}