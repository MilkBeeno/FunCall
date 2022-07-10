package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.milk.funcall.R
import com.milk.funcall.account.ui.adapter.FansOrFollowsAdapter
import com.milk.funcall.account.ui.vm.FollowsViewModel
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityFollowsBinding
import com.milk.simple.ktx.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowsActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityFollowsBinding>()
    private val followsViewModel by viewModels<FollowsViewModel>()
    private val followsAdapter by lazy { FansOrFollowsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(R.string.mine_fans)
        binding.rvFollows.layoutManager = GridLayoutManager(this, 2)
        binding.rvFollows.addItemDecoration(SimpleGridDecoration(this))
        binding.rvFollows.adapter = followsAdapter
        binding.tvAttention.setOnClickListener(this)
    }

    private fun initializeData() {
        followsAdapter.addRefreshedListener {
            if (it == RefreshStatus.Success && followsAdapter.itemCount > 0)
                binding.llFollowsEmpty.gone()
            else
                binding.llFollowsEmpty.visible()
        }
        lifecycleScope.launch {
            followsViewModel.pagingSource.flow.collectLatest { followsAdapter.submitData(it) }
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.tvAttention -> {

            }
        }
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, FollowsActivity::class.java))
        }
    }
}