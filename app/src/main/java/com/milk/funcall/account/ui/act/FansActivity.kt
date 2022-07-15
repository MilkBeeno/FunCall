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
import com.milk.funcall.account.ui.vm.FansViewModel
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityFansBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.simple.ktx.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FansActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityFansBinding>()
    private val fansViewModel by viewModels<FansViewModel>()
    private val fansAdapter by lazy { FansOrFollowsAdapter() }
    private val loadingDialog by lazy { LoadingDialog(this, string(R.string.common_loading)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.root.navigationBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(R.string.mine_fans)
        binding.rvFans.layoutManager = GridLayoutManager(this, 2)
        binding.rvFans.addItemDecoration(SimpleGridDecoration(this))
        binding.rvFans.adapter = fansAdapter
        binding.tvPublish.setOnClickListener(this)
    }

    private fun initializeData() {
        loadingDialog.show()
        fansAdapter.addRefreshedListener {
            loadingDialog.dismiss()
            if (it == RefreshStatus.Success && fansAdapter.itemCount > 0)
                binding.llFanEmpty.gone()
            else
                binding.llFanEmpty.visible()
        }
        lifecycleScope.launch {
            fansViewModel.pagingSource.flow.collectLatest { fansAdapter.submitData(it) }
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.tvPublish -> {

            }
        }
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, FansActivity::class.java))
        }
    }
}