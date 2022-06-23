package com.milk.funcall.main.ui.frag

import android.view.View
import com.milk.funcall.R
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentMineBinding

class MineFragment : AbstractFragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }

    override fun getRootView(): View = binding.root

    override fun initializeView() {
        binding.editProfile.setOptionResource(
            image = R.drawable.mine_edit_profile,
            string = R.string.mine_edit_profile
        )
        binding.blackedList.setOptionResource(
            image = R.drawable.mine_blacked_list,
            string = R.string.mine_blacked_list
        )
        binding.aboutUs.setOptionResource(
            image = R.drawable.mine_about_us,
            string = R.string.mine_about_us
        )
        binding.signOut.setOptionResource(
            image = R.drawable.mine_sign_out,
            string = R.string.mine_sign_out,
            showLine = false
        )
    }

    companion object {
        fun create() = MineFragment()
    }
}