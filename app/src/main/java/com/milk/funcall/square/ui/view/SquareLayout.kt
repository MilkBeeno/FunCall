package com.milk.funcall.square.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.milk.funcall.account.Account
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.databinding.LayoutSquareBinding
import com.milk.funcall.user.status.Gender
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.visible

class SquareLayout : FrameLayout {
    private val binding = LayoutSquareBinding
        .inflate(LayoutInflater.from(context), this, true)

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    internal fun setUserAvatars(avatars: MutableList<String>) {
        val targetGender = if (Account.userGender == Gender.Man.value) {
            Gender.Woman.value
        } else {
            Gender.Man.value
        }
        // 1
        if (avatars.isNotEmpty()) {
            binding.ivFirstUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[0], targetGender)
                .target(binding.ivFirstUser).build()
        } else binding.ivFirstUser.gone()
        // 2
        if (avatars.size > 1) {
            binding.ivSecondUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[1], targetGender)
                .target(binding.ivSecondUser).build()
        } else binding.ivSecondUser.gone()
        // 3
        if (avatars.size > 2) {
            binding.ivThirdUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[2], targetGender)
                .target(binding.ivThirdUser).build()
        } else binding.ivThirdUser.gone()
        // 4
        if (avatars.size > 3) {
            binding.ivFourthUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[3], targetGender)
                .target(binding.ivFourthUser).build()
        } else binding.ivFourthUser.gone()
        // 5
        if (avatars.size > 4) {
            binding.ivFifthUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[4], targetGender)
                .target(binding.ivFifthUser).build()
        } else binding.ivFifthUser.gone()
        // 6
        if (avatars.size > 5) {
            binding.ivSixthUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[5], targetGender)
                .target(binding.ivSixthUser).build()
        } else binding.ivSixthUser.gone()
        // 7
        if (avatars.size > 6) {
            binding.ivSeventhUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[6], targetGender)
                .target(binding.ivSeventhUser).build()
        } else binding.ivSeventhUser.gone()
        // 8
        if (avatars.size > 7) {
            binding.ivEighthUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[7], targetGender)
                .target(binding.ivEighthUser).build()
        } else binding.ivEighthUser.gone()
        // 9
        if (avatars.size > 8) {
            binding.ivNinthUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[8], targetGender)
                .target(binding.ivNinthUser).build()
        } else binding.ivNinthUser.gone()
        // 10
        if (avatars.size > 9) {
            binding.ivTenthUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[9], targetGender)
                .target(binding.ivTenthUser).build()
        } else binding.ivTenthUser.gone()
        // 11
        if (avatars.size > 10) {
            binding.ivEleventhUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[10], targetGender)
                .target(binding.ivEleventhUser).build()
        } else binding.ivEleventhUser.gone()
        // 12
        if (avatars.size > 11) {
            binding.ivTwelfthUser.visible()
            ImageLoader.Builder().loadAvatar(avatars[11], targetGender)
                .target(binding.ivTwelfthUser).build()
        } else binding.ivTwelfthUser.gone()
    }
}