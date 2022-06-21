package com.milk.funcall.main.ui.frag

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentHomeBinding
import com.milk.funcall.main.ui.adapter.HomeAdapter
import com.milk.funcall.main.ui.vm.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : AbstractFragment() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val adapter by lazy { HomeAdapter() }

    override fun getRootView(): View = binding.root

    override fun initializeView() {
        binding.rvHome.adapter = adapter.withLoadStateAdapter()
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
    }

    companion object {
        fun create() = HomeFragment()
    }
}