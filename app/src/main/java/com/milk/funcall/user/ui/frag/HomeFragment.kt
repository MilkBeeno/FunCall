package com.milk.funcall.user.ui.frag

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.paging.StaggeredGridDecoration
import com.milk.funcall.common.paging.status.AppendStatus
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentHomeBinding
import com.milk.funcall.user.ui.adapter.HomeAdapter
import com.milk.funcall.user.ui.vm.HomeViewModel
import com.milk.simple.log.Logger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : AbstractFragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val adapter by lazy { HomeAdapter() }
    private val itemDecoration by lazy {
        StaggeredGridDecoration(requireContext(), 10, 4)
    }
    private val layoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getRootView(): View = binding.root

    override fun initializeData() {
        super.initializeData()
        lifecycleScope.launch {
            homeViewModel.pagingSource.flow.collectLatest { adapter.submitData(it) }
        }
    }

    override fun initializeObserver() {
        super.initializeObserver()
        LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST)
            .observe(this) { binding.refresh.autoRefresh() }
    }

    override fun initializeView() {
        binding.headerToolbar.setTitle(R.string.home_title)
        binding.rvHome.itemAnimator = null
        binding.rvHome.layoutManager = layoutManager
        binding.rvHome.addItemDecoration(itemDecoration)
        binding.rvHome.adapter = adapter.withLoadStateFooterAdapter()
        binding.refresh.setRefreshHeader(binding.refreshHeader)
        binding.refresh.setOnRefreshListener { adapter.refresh() }
        adapter.addRefreshedListener {
            binding.refresh.finishRefresh(1500)
            if (adapter.itemCount > 0) layoutManager.scrollToPosition(0)
            when (it) {
                RefreshStatus.Success -> {
                    Logger.d("当前数据加载成功", "hlc")
                }
                RefreshStatus.Error -> {
                    Logger.d("当前数据加载失败", "hlc")
                }
                RefreshStatus.Failed -> {
                    Logger.d("有数据刷新失败", "hlc")
                }
                RefreshStatus.Empty -> {
                    Logger.d("当前是空数据哦", "hlc")
                }
            }
        }
        adapter.addAppendedListener {
            when (it) {
                AppendStatus.Failed -> {
                    Logger.d("加载更多数据失败", "hlc")
                }
                AppendStatus.Success -> {
                    Logger.d("加载更多数据成功", "hlc")
                }
            }
        }
    }

    companion object {
        fun create() = HomeFragment()
    }
}