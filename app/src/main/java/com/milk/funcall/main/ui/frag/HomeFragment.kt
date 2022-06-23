package com.milk.funcall.main.ui.frag

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.paging.status.AppendStatus
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentHomeBinding
import com.milk.funcall.main.ui.adapter.HomeAdapter
import com.milk.funcall.main.ui.vm.HomeViewModel
import com.milk.simple.log.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : AbstractFragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val adapter by lazy { HomeAdapter() }

    override fun getRootView(): View = binding.root

    override fun initializeObserver() {
        super.initializeObserver()
        LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST)
            .observe(this) {
                binding.refresh.isRefreshing = true
                adapter.refresh()
            }
    }

    override fun initializeView() {
        binding.rvHome.adapter = adapter.withLoadStateFooterAdapter()
        binding.rvHome.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            delay(3000)
            homeViewModel.pagingSource.flow.collectLatest {
                adapter.submitData(it)
            }
        }
        binding.refresh.setOnRefreshListener {
            adapter.refresh()
        }
        adapter.addRefreshedListener {
            binding.refresh.isRefreshing = false
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