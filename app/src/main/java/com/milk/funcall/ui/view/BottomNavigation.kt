package com.milk.funcall.ui.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import com.milk.funcall.R
import com.milk.funcall.databinding.ViewBottomNavigationBinding

class BottomNavigation : FrameLayout {

    private val binding = ViewBottomNavigationBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var lastClickTime: Long = 0
    private var lastSelectType: Type? = null
    private val clickMinTimeInterval: Long = 300
    private var itemOnClickListener: ((Boolean, Type) -> Unit)? = null

    private val zoomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.anim_nav_zoom)
    }
    private val alphaAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.anim_nav_alpha)
    }

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    init {
        updateSelectNav(Type.Home)
    }

    fun setItemOnClickListener(listener: ((Boolean, Type) -> Unit)) {
        itemOnClickListener = listener
    }

    private fun updateSelectNav(type: Type) {
        when (type) {
            Type.Home -> {
                updateHomeNav(true)
                updateMessageNav(false)
                updateMineNav(false)
                lastSelectType = Type.Home
            }
            Type.Message -> {
                updateHomeNav(false)
                updateMessageNav(true)
                updateMineNav(false)
                lastSelectType = Type.Message
            }
            Type.Mine -> {
                updateHomeNav(false)
                updateMessageNav(false)
                updateMineNav(true)
                lastSelectType = Type.Mine
            }
        }
    }

    private fun updateHomeNav(select: Boolean = false) {
        if (select) {
            binding.ivHomeSmall.visibility = GONE
            binding.tvHome.visibility = GONE
            binding.ivHomeMedium.visibility = VISIBLE
            binding.ivHomeMedium.setImageResource(R.drawable.main_nav_hone_select)
            binding.ivHomeMedium.startAnimation(zoomAnimation)
            binding.ivHomeSmall.startAnimation(alphaAnimation)
        } else {
            binding.ivHomeSmall.visibility = VISIBLE
            binding.tvHome.visibility = VISIBLE
            binding.ivHomeMedium.visibility = GONE
            binding.ivHomeSmall.setImageResource(R.drawable.main_nav_hone)
            binding.tvHome.setTextColor(getColor(R.color.FF5B5D66))
        }
        binding.llHome.backPressureClickListener {
            if (lastSelectType == Type.Home)
                itemOnClickListener?.invoke(true, Type.Home)
            else {
                itemOnClickListener?.invoke(false, Type.Home)
                updateSelectNav(Type.Home)
            }
        }
    }

    private fun updateMessageNav(select: Boolean = false) {
        if (select) binding.ivMessage.startAnimation(zoomAnimation)
        binding.ivMessage.setImageResource(
            if (select) R.drawable.main_nav_message_select else R.drawable.main_nav_message
        )
        binding.tvMessage.setTextColor(
            if (select) getColor(R.color.FF8E58FB) else getColor(R.color.FF5B5D66)
        )
        binding.clMessage.backPressureClickListener {
            if (lastSelectType == Type.Message)
                itemOnClickListener?.invoke(true, Type.Message)
            else {
                itemOnClickListener?.invoke(false, Type.Message)
                updateSelectNav(Type.Message)
            }
        }
    }

    private fun updateMineNav(select: Boolean = false) {
        if (select) binding.ivMine.startAnimation(zoomAnimation)
        binding.ivMine.setImageResource(
            if (select) R.drawable.main_nav_mine_select else R.drawable.main_nav_mine
        )
        binding.tvMine.setTextColor(
            if (select) getColor(R.color.FF8E58FB) else getColor(R.color.FF5B5D66)
        )
        binding.llMine.backPressureClickListener {
            if (lastSelectType == Type.Mine)
                itemOnClickListener?.invoke(true, Type.Mine)
            else {
                itemOnClickListener?.invoke(false, Type.Mine)
                updateSelectNav(Type.Mine)
            }
        }
    }

    private fun ViewGroup.backPressureClickListener(action: () -> Unit) {
        setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > clickMinTimeInterval) {
                action()
                lastClickTime = currentTime
            }
        }
    }

    private fun getColor(@ColorRes resId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            resources.getColor(resId, context.theme)
        else
            resources.getColor(resId)
    }

    enum class Type { Home, Message, Mine }
}