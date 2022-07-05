package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.milk.funcall.R
import com.milk.funcall.account.ui.adapter.FansAdapter
import com.milk.funcall.account.ui.vm.FansViewModel
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityFansBinding
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.viewBinding
import com.milk.simple.ktx.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FansActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityFansBinding>()
    private val fansViewModel by viewModels<FansViewModel>()
    private val fansAdapter by lazy { FansAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        immersiveStatusBar(binding.headerToolbar)
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(R.string.mine_fans)
        binding.rvFans.layoutManager = GridLayoutManager(this, 2)
        binding.rvFans.addItemDecoration(SimpleGridDecoration(this))
        binding.rvFans.adapter = fansAdapter
    }

    private fun initializeData() {
        fansAdapter.addRefreshedListener {
            if (it == RefreshStatus.Success && fansAdapter.itemCount > 0)
                binding.llFanEmpty.gone()
            else
                binding.llFanEmpty.visible()
        }
        lifecycleScope.launch {
            fansViewModel.pagingSource.flow.collectLatest { fansAdapter.submitData(it) }
        }
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, FansActivity::class.java))
        }
    }
}