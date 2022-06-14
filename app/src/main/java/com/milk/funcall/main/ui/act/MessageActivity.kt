package com.milk.funcall.main.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.milk.funcall.R
import com.milk.funcall.databinding.ActivityMessageBinding
import com.milk.funcall.main.ui.adapter.MessageAdapter
import com.milk.funcall.main.ui.vm.MessageViewModel
import com.milk.simple.ktx.color
import com.milk.simple.ktx.setStatusBarColor
import com.milk.simple.ktx.setStatusBarDark
import com.milk.simple.ktx.viewBinding

class MessageActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityMessageBinding>()
    private val messageViewModel by viewModels<MessageViewModel>()
    private val messageAdapter by lazy { MessageAdapter() }
    private val targetId by lazy { intent.getLongExtra(TARGET_ID, 0) }
    private val targetName by lazy { intent.getStringExtra(TARGET_NAME).toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarDark()
        setStatusBarColor(color(R.color.white))
        initializeView()
    }

    private fun initializeView() {
        binding.headerToolbar.setTitle(targetName)
        binding.headerToolbar.clickArrowBack()
        binding.rvMessage.adapter = messageAdapter
        binding.rvMessage.layoutManager = LinearLayoutManager(this)
        messageAdapter.setPagerSource(messageViewModel.obtainPagingSource(targetId))
    }

    companion object {
        private const val TARGET_ID = "TARGET_ID"
        private const val TARGET_NAME = "TARGET_NAME"
        fun create(context: Context, targetId: Long, targetName: String) {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra(TARGET_ID, targetId)
            intent.putExtra(TARGET_NAME, targetName)
            context.startActivity(intent)
        }
    }
}