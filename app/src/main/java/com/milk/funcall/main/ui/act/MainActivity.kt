package com.milk.funcall.main.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityMainBinding
import com.milk.funcall.main.ui.frag.HomeFragment
import com.milk.funcall.main.ui.frag.MessageFragment
import com.milk.funcall.main.ui.frag.MineFragment
import com.milk.funcall.main.ui.view.BottomNavigation
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.viewBinding

class MainActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityMainBinding>()

    private val fragments = mutableListOf<Fragment>()
    private val homeFragment = HomeFragment.create()
    private val messageFragment = MessageFragment.create()
    private val mineFragment = MineFragment.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar(binding.flContent)
        initializeView()
    }

    private fun initializeView() {
        setTabSelection(homeFragment)
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
                binding.background.setFullBackground()
            }
            is MessageFragment -> {
                if (!fragments.contains(messageFragment)) {
                    fragments.add(messageFragment)
                    transaction.add(binding.flContent.id, messageFragment)
                }
                transaction.show(messageFragment)
                binding.background.setFullBackground()
            }
            is MineFragment -> {
                if (!fragments.contains(mineFragment)) {
                    fragments.add(mineFragment)
                    transaction.add(binding.flContent.id, mineFragment)
                }
                transaction.show(mineFragment)
                binding.background.setMediumBackground()
            }
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        transaction.hide(homeFragment)
        transaction.hide(messageFragment)
        transaction.hide(mineFragment)
    }

    override fun onInterceptKeyDownEvent(): Boolean = true

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }
}