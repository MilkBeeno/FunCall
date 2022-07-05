package com.milk.funcall.user.ui.frag

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.paging.StaggeredGridDecoration
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentHomeBinding
import com.milk.funcall.user.ui.adapter.HomeAdapter
import com.milk.funcall.user.ui.vm.HomeViewModel
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.showToast
import com.milk.simple.ktx.string
import com.milk.simple.ktx.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : AbstractFragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val homeViewModel by viewModels<HomeViewModel>()
    private val adapter by lazy { HomeAdapter() }

    override fun getRootView(): View = binding.root

    override fun initializeData() {
        super.initializeData()
        adapter.addRefreshedListener {
            binding.refresh.finishRefresh(1500)
            if (adapter.itemCount > 0)
                binding.rvHome.scrollToPosition(0)
            when (it) {
                RefreshStatus.Success -> binding.homeNothing.root.gone()
                else -> {
                    if (adapter.itemCount > 0) {
                        binding.homeNothing.root.gone()
                        requireContext().showToast(
                            requireContext().string(R.string.home_list_refresh_failed)
                        )
                    } else binding.homeNothing.root.visible()
                }
            }
        }
        lifecycleScope.launch {
            homeViewModel.pagingSource.flow.collectLatest { adapter.submitData(it) }
        }
    }

    override fun initializeObserver() {
        super.initializeObserver()
        LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST)
            .observe(this) {
                if (adapter.itemCount > 0)
                    binding.rvHome.smoothScrollToPosition(0)
                binding.refresh.autoRefresh()
            }
    }

    override fun initializeView() {
        binding.headerToolbar.setTitle(R.string.home_title)
        binding.rvHome.itemAnimator = null
        binding.rvHome.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvHome.addItemDecoration(
            StaggeredGridDecoration(requireContext(), 10, 4)
        )
        binding.rvHome.adapter = adapter.withLoadStateFooterAdapter()
        binding.refresh.setRefreshHeader(binding.refreshHeader)
        binding.refresh.setOnRefreshListener { adapter.refresh() }
    }

    companion object {
        fun create() = HomeFragment()
    }
}