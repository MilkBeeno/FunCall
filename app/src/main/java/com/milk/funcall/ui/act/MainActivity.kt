package com.milk.funcall.ui.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.milk.funcall.databinding.ActivityMainBinding
import com.milk.funcall.ui.frag.HomeFragment
import com.milk.funcall.ui.frag.MessageFragment
import com.milk.funcall.ui.frag.MineFragment
import com.milk.funcall.ui.view.BottomNavigation

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val fragments = mutableListOf<Fragment>()
    private val homeFragment = HomeFragment.create()
    private val messageFragment = MessageFragment.create()
    private val mineFragment = MineFragment.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        setTabSelection(homeFragment)
        binding.navigation.setItemOnClickListener { refresh, type ->
            when (type) {
                BottomNavigation.Type.Home -> {
                    if (refresh) {
                        // 刷新数据
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
            }
            is MessageFragment -> {
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
}