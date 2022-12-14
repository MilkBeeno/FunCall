package com.milk.funcall.app.ui.act

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FCMMessagingService
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.pay.PayManager
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.util.NotificationUtil
import com.milk.funcall.databinding.ActivityMainBinding
import com.milk.funcall.square.ui.frag.SquareFragment
import com.milk.funcall.user.ui.frag.HomeFragment
import com.milk.simple.ktx.collect
import com.milk.simple.ktx.collectLatest
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.navigationBarPadding
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
    private val squareFragment = SquareFragment.create()
    private var serviceIntent: Intent? = null
    private var connection: ServiceConnection? = null
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        initializeService()
        setNotificationConfig()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setTabSelection(homeFragment)
        binding.navigation.updateSelectNav(BottomNavigation.Type.Home)
        FCMMessagingService.uploadNewToken()
    }

    private fun initializeView() {
        immersiveStatusBar()
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
                BottomNavigation.Type.Square -> {
                    setTabSelection(squareFragment)
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
                        // ???????????????????????????????????????????????????
                        // ?????????????????????????????????????????????????????????
                        // Logger.d("?????????????????????", "IM-Service")
                        val timerTask = object : TimerTask() {
                            override fun run() {
                                Logger.d(
                                    "IM Okhttp ????????? ????????????=${System.currentTimeMillis()}", "IM-Service"
                                )
                                MessageRepository.heartBeat()
                            }
                        }
                        timer = Timer()
                        timer?.schedule(timerTask, 0, 5000)
                    }

                    override fun onServiceDisconnected(p0: ComponentName?) {
                        // ?????????????????????????????????
                        // ?????????unbindService???????????????
                        // Logger.d("?????????????????????", "IM-Service")
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
        PayManager.googlePay.updateSubscribeStatus(this)
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
            is SquareFragment -> {
                if (!fragments.contains(squareFragment)) {
                    fragments.add(squareFragment)
                    transaction.add(binding.flContent.id, squareFragment)
                }
                transaction.show(squareFragment)
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
        transaction.hide(squareFragment)
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