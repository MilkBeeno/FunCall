package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.milk.funcall.R
import com.milk.funcall.account.ui.adapter.BlackedListAdapter
import com.milk.funcall.account.ui.vm.BlackedViewModel
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityBlackedListBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.simple.ktx.*

class BlackedListActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityBlackedListBinding>()
    private val blackedViewModel by viewModels<BlackedViewModel>()
    private val blackedListAdapter by lazy { BlackedListAdapter() }
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
        binding.headerToolbar.setTitle(R.string.mine_blacked_list)
        binding.rvBlackList.layoutManager = GridLayoutManager(this, 4)
        binding.rvBlackList.adapter = blackedListAdapter
        blackedListAdapter.setOnItemChildClickListener { adapter, _, position ->
            UserInfoActivity
                .create(this, adapter.getNoNullItem(position).userId)
        }
    }

    private fun initializeData() {
        loadingDialog.show()
        blackedListAdapter.addRefreshedListener {
            loadingDialog.dismiss()
            if (it == RefreshStatus.Success && blackedListAdapter.itemCount > 0)
                binding.ivEmpty.gone()
            else
                binding.ivEmpty.visible()
        }
        blackedViewModel.pagingSource.flow
            .collectLatest(this) {
                blackedListAdapter.submitData(it)
            }
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, BlackedListActivity::class.java))
        }
    }
}