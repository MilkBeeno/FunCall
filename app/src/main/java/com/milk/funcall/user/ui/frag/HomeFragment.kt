package com.milk.funcall.user.ui.frag

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.paging.StaggeredGridDecoration
import com.milk.funcall.common.paging.status.RefreshStatus
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentHomeBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.funcall.user.ui.adapter.HomeAdapter
import com.milk.funcall.user.ui.dialog.SayHiDialog
import com.milk.funcall.user.ui.dialog.ViewAdDialog
import com.milk.funcall.user.ui.vm.HomeViewModel
import com.milk.simple.ktx.*
import com.milk.simple.mdr.KvManger

class HomeFragment : AbstractFragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val homeViewModel by viewModels<HomeViewModel>()
    private val adapter by lazy { HomeAdapter() }
    private val loadingDialog by lazy { LoadingDialog(requireActivity()) }
    private val sayHiDialog by lazy { SayHiDialog(requireActivity()) }
    private val viewAdDialog by lazy { ViewAdDialog(requireActivity()) }
    private var adView: View? = null

    override fun getRootView(): View = binding.root

    @SuppressLint("NotifyDataSetChanged")
    override fun initializeData() {
        super.initializeData()
        checkNewClientOnHome()
        loadingDialog.show()
        binding.headerToolbar.statusBarPadding()
        homeViewModel.loadSayHiAd(requireActivity())
        adapter.addRefreshedListener {
            loadingDialog.dismiss()
            binding.refresh.finishRefresh(1500)
            if (adapter.itemCount > 0)
                binding.rvHome.scrollToPosition(0)
            when (it) {
                RefreshStatus.Success -> {
                    binding.homeNothing.root.gone()
                }
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
        homeViewModel.pagingSource.flow
            .collectLatest(this) { adapter.submitData(it) }
    }

    private fun checkNewClientOnHome() {
        val checkNewClientOnHome =
            KvManger.getBoolean(KvKey.CHECK_NEW_CLIENT_ON_HOME, true)
        if (checkNewClientOnHome) {
            KvManger.put(KvKey.CHECK_NEW_CLIENT_ON_HOME, false)
            FireBaseManager.logEvent(FirebaseKey.FIRST_OPEN_HOME_PAGE)
        }
    }

    override fun initializeObserver() {
        super.initializeObserver()
        LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST).observe(this) {
            if (adapter.itemCount > 0) {
                binding.rvHome.smoothScrollToPosition(0)
            }
            binding.refresh.autoRefresh()
        }
        homeViewModel.sayHiFlow.collectLatest(this) {
            if (it.isNotEmpty()) {
                FireBaseManager.logEvent(FirebaseKey.SAY_HI_PAGE_SHOW)
                sayHiDialog.show()
                sayHiDialog.setUserList(it)
                sayHiDialog.setOnConfirmListener {
                    FireBaseManager.logEvent(FirebaseKey.ENTER_SAY_HI)
                    viewAdDialog.show()
                    viewAdDialog.setOnConfirmRequest {
                        homeViewModel.showSayHiAd(requireActivity())
                        homeViewModel.sendTextMessage(requireContext(), it)
                    }
                }
            }
        }
        Account.userSubscribeFlow.collectLatest(this) {
            if (it && AdConfig.adCancelType == 2 && adView?.parent != null) {
                binding.root.removeView(adView)
            } else initializeAdView()
        }
    }

    override fun initializeView() {
        FireBaseManager.logEvent(FirebaseKey.THE_HOME_PAGE_SHOW)
        binding.headerToolbar.setTitle(R.string.home_title)
        binding.rvHome.itemAnimator = null
        binding.rvHome.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvHome.addItemDecoration(
            StaggeredGridDecoration(requireContext(), 10, 4)
        )
        binding.rvHome.adapter = adapter.withLoadStateFooterAdapter()
        binding.refresh.setRefreshHeader(binding.refreshHeader)
        binding.refresh.setOnRefreshListener {
            if (!AdConfig.isLoadedAds()) AdConfig.obtain()
            adapter.refresh()
        }
        adapter.setOnItemClickListener { adapter, _, position ->
            FireBaseManager.logEvent(FirebaseKey.CLICK_THE_AVATAR)
            val user = adapter.getNoNullItem(position)
            if (user.targetId > 0) UserInfoActivity.create(requireContext(), user.targetId)
        }
    }

    private fun initializeAdView() {
        try {
            if (adView?.parent != null) return
            val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.MAIN_HOME_BOTTOM)
            if (adUnitId.isNotBlank() && AdConfig.adCancelType != 2) {
                FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_4)
                adView = AdManager.loadBannerAd(
                    activity = requireActivity(),
                    adUnitId = adUnitId,
                    loadFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_4, adUnitId, it)
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_4)
                    },
                    showFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_4, adUnitId, it)
                    },
                    showSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_4)
                    },
                    clickRequest = {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_AD_4)
                    })
                binding.root.addView(adView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        internal fun create() = HomeFragment()
    }
}