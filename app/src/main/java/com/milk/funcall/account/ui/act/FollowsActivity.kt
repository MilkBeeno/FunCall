package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.ui.adapter.FansOrFollowsAdapter
import com.milk.funcall.account.ui.vm.FollowsViewModel
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityFollowsBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.simple.ktx.*

class FollowsActivity : AbstractActivity() {
    private val binding by lazy { ActivityFollowsBinding.inflate(layoutInflater) }
    private val followsViewModel by viewModels<FollowsViewModel>()
    private val followsAdapter by lazy { FansOrFollowsAdapter() }
    private val loadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        immersiveStatusBar()
        loadingDialog.show()
        binding.headerToolbar.statusBarPadding()
        binding.root.navigationBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(R.string.mine_follows)
        binding.rvFollows.layoutManager = GridLayoutManager(this, 2)
        binding.rvFollows.addItemDecoration(SimpleGridDecoration(this))
        binding.rvFollows.adapter = followsAdapter
        binding.tvAttention.setOnClickListener(this)
        followsAdapter.setOnItemClickListener { adapter, _, position ->
            UserInfoActivity.create(this, adapter.getNoNullItem(position).targetId)
        }
    }

    private fun initializeData() {
        FireBaseManager.logEvent(FirebaseKey.FOLLOW_SHOW)
        followsAdapter.addRefreshedListener {
            loadingDialog.dismiss()
            if (it == RefreshStatus.Success && followsAdapter.itemCount > 0) {
                binding.llFollowsEmpty.gone()
            } else {
                binding.llFollowsEmpty.visible()
            }
        }
        followsViewModel.pagingSource.flow.collectLatest(this) {
            followsAdapter.submitData(it)
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.tvAttention -> {
                finish()
                LiveEventBus.get<Any?>(EventKey.JUMP_TO_THE_HOME_PAGE).post(null)
            }
        }
    }

    companion object {
        internal fun create(context: Context) {
            context.startActivity(Intent(context, FollowsActivity::class.java))
        }
    }
}