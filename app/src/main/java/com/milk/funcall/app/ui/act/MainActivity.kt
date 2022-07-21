package com.milk.funcall.app.ui.act

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.frag.MineFragment
import com.milk.funcall.app.MainService
import com.milk.funcall.app.ui.view.BottomNavigation
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.chat.ui.frag.ConversationFragment
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityMainBinding
import com.milk.funcall.user.ui.frag.HomeFragment
import com.milk.simple.ktx.*
import com.milk.simple.log.Logger
import kotlinx.coroutines.flow.collectLatest
import java.util.*

class MainActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityMainBinding>()
    private val fragments = mutableListOf<Fragment>()
    private val homeFragment = HomeFragment.create()
    private val messageFragment = ConversationFragment.create()
    private val mineFragment = MineFragment.create()
    private var serviceIntent: Intent? = null
    private var connection: ServiceConnection? = null
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeService()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setTabSelection(homeFragment)
        binding.navigation.updateSelectNav(BottomNavigation.Type.Home)
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
                        LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST)
                            .post(true)
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

    private fun initializeService() {
        launch {
            Account.userLoggedFlow.collectLatest { isLogged ->
                if (isLogged) {
                    connection = object : ServiceConnection {
                        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                            // 和服务绑定成功后，服务会回调该方法
                            // 服务异常中断后重启，也会重新调用改方法
                            // Logger.d("服务初始化完成", "IM-Service")
                            val timerTask = object : TimerTask() {
                                override fun run() {
                                    Logger.d(
                                        "IM Okhttp 心跳包 当前时间=${System.currentTimeMillis()}",
                                        "IM-Service"
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
        transaction.commit()
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
        fun create(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }
}