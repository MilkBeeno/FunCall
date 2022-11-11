package com.milk.funcall.app.ui.act

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.frag.MineFragment
import com.milk.funcall.app.MainService
import com.milk.funcall.app.ui.MainViewModel
import com.milk.funcall.app.ui.dialog.NotificationDialog
import com.milk.funcall.app.ui.view.BottomNavigation
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.chat.ui.frag.ConversationFragment
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FCMMessagingService
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.util.NotificationUtil
import com.milk.funcall.databinding.ActivityMainBinding
import com.milk.funcall.user.ui.frag.HomeFragment
import com.milk.simple.ktx.*
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger
import java.util.*

class MainActivity : AbstractActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel by viewModels<MainViewModel>()
    private val fragments = mutableListOf<Fragment>()
    private val homeFragment = HomeFragment.create()
    private val messageFragment = ConversationFragment.create()
    private val mineFragment = MineFragment.create()
    private var serviceIntent: Intent? = null
    private var connection: ServiceConnection? = null
    private var timer: Timer? = null
    private var adView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        initializeService()
        setNotificationConfig()
    }

    private fun initializeAdView() {
        try {
            if (adView?.parent != null) return
            val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.MAIN_HOME_BOTTOM)
            if (adUnitId.isNotBlank() && AdConfig.adCancelType != 2) {
                FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_4)
                adView = AdManager.loadBannerAd(activity = this,
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setTabSelection(homeFragment)
        binding.navigation.updateSelectNav(BottomNavigation.Type.Home)
        FCMMessagingService.uploadNewToken()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.flContent.statusBarPadding()
        binding.root.navigationBarPadding()
        setTabSelection(homeFragment)
        binding.navigation.updateSelectNav(BottomNavigation.Type.Home)
        binding.navigation.setItemOnClickListener { refresh, type ->
            when (type) {
                BottomNavigation.Type.Home -> {
                    if (refresh) {
                        LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST).post(true)
                    } else setTabSelection(homeFragment)
                }
                BottomNavigation.Type.Message -> {
                    setTabSelection(messageFragment)
                }
                BottomNavigation.Type.Mine -> {
                    setTabSelection(mineFragment)
                }
            }
        }
    }

    private fun initializeObserver() {
        LiveEventBus.get<Any?>(EventKey.JUMP_TO_THE_HOME_PAGE).observe(this) {
            setTabSelection(homeFragment)
            binding.navigation.updateSelectNav(BottomNavigation.Type.Home)
        }
        Account.userSubscribeFlow.collectLatest(this) {
            if (it && AdConfig.adCancelType == 2 && adView?.parent != null) {
                binding.root.removeView(adView)
            } else initializeAdView()
        }
    }

    private fun initializeService() {
        Account.userIdFlow.collect(this) { accountId ->
            mainViewModel.getConversationCount().collectLatest(this) { countList ->
                var count = 0
                countList?.forEach { count += it }
                binding.navigation.updateUnReadCount(count)
            }
            if (accountId > 0) {
                connection = object : ServiceConnection {
                    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                        // 和服务绑定成功后，服务会回调该方法
                        // 服务异常中断后重启，也会重新调用改方法
                        // Logger.d("服务初始化完成", "IM-Service")
                        val timerTask = object : TimerTask() {
                            override fun run() {
                                Logger.d(
                                    "IM Okhttp 心跳包 当前时间=${System.currentTimeMillis()}", "IM-Service"
                                )
                                MessageRepository.heartBeat()
                            }
                        }
                        timer = Timer()
                        timer?.schedule(timerTask, 0, 5000)
                    }

                    override fun onServiceDisconnected(p0: ComponentName?) {
                        // 当服务异常终止时会调用
                        // 注意，unbindService时不会调用
                        // Logger.d("服务异常终止了", "IM-Service")
                    }
                }
                serviceIntent = Intent(this, MainService::class.java)
                connection?.let { bindService(serviceIntent, it, BIND_AUTO_CREATE) }
            } else {
                timer?.cancel()
                connection?.let { unbindService(it) }
            }
        }
    }

    private fun setNotificationConfig() {
        FCMMessagingService.uploadNewToken()
        if (!NotificationUtil.isEnabled(this)) {
            FireBaseManager.logEvent(FirebaseKey.OPEN_NOTIFICATION_REQUEST_POPUP_SHOW)
            val alreadySet = KvManger.getBoolean(KvKey.ALREADY_SET_NOTIFICATION)
            NotificationDialog(this) {
                if (alreadySet) {
                    NotificationUtil.toSystemSetting(this)
                } else {
                    KvManger.put(KvKey.ALREADY_SET_NOTIFICATION, true)
                    NotificationUtil.getPermission(this)
                }
            }.show()
        } else {
            KvManger.put(KvKey.ALREADY_SET_NOTIFICATION, true)
        }
    }

    private fun setTabSelection(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (fragment) {
            is HomeFragment -> {
                if (!fragments.contains(homeFragment)) {
                    fragments.add(homeFragment)
                    transaction.add(binding.flContent.id, homeFragment)
                }
                transaction.show(homeFragment)
            }
            is ConversationFragment -> {
                if (!fragments.contains(messageFragment)) {
                    fragments.add(messageFragment)
                    transaction.add(binding.flContent.id, messageFragment)
                }
                transaction.show(messageFragment)
            }
            is MineFragment -> {
                if (!fragments.contains(mineFragment)) {
                    fragments.add(mineFragment)
                    transaction.add(binding.flContent.id, mineFragment)
                }
                transaction.show(mineFragment)
            }
        }
        transaction.commitAllowingStateLoss()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        transaction.hide(homeFragment)
        transaction.hide(messageFragment)
        transaction.hide(mineFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        connection?.let { unbindService(it) }
    }

    override fun onInterceptKeyDownEvent(): Boolean = true

    companion object {
        internal fun create(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }
}