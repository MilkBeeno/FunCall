package com.milk.funcall.app.ui.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import com.milk.funcall.R
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.databinding.LayoutBottomNavigationBinding

class BottomNavigation : FrameLayout, OnClickListener {
    private val binding = LayoutBottomNavigationBinding
        .inflate(LayoutInflater.from(context), this, true)
    private var lastClickTime: Long = 0
    private var lastSelectType: Type = Type.Home
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
        binding.llHome.setOnClickListener(this)
        binding.llSquare.setOnClickListener(this)
        binding.clMessage.setOnClickListener(this)
        binding.llMine.setOnClickListener(this)
    }

    internal fun setItemOnClickListener(listener: ((Boolean, Type) -> Unit)) {
        itemOnClickListener = listener
    }

    internal fun updateSelectNav(type: Type) {
        when (type) {
            Type.Home -> {
                updateHomeNav(true)
                updateSquareNav(false)
                updateMessageNav(false)
                updateMineNav(false)
                lastSelectType = Type.Home
            }
            Type.Square -> {
                updateHomeNav(false)
                updateSquareNav(true)
                updateMessageNav(false)
                updateMineNav(false)
                lastSelectType = Type.Square
            }
            Type.Message -> {
                updateHomeNav(false)
                updateSquareNav(false)
                updateMessageNav(true)
                updateMineNav(false)
                lastSelectType = Type.Message
            }
            Type.Mine -> {
                updateHomeNav(false)
                updateSquareNav(false)
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
            binding.ivHomeMedium.clearAnimation()
        }
    }

    private fun updateSquareNav(select: Boolean = false) {
        if (select) {
            binding.ivSquare.startAnimation(zoomAnimation)
            binding.tvSquare.setTextColor(getColor(R.color.FF8E58FB))
            binding.ivSquare.setImageResource(R.drawable.main_nav_square_select)
        } else {
            binding.ivSquare.clearAnimation()
            binding.tvSquare.setTextColor(getColor(R.color.FF5B5D66))
            binding.ivSquare.setImageResource(R.drawable.main_nav_square)
        }
    }

    private fun updateMessageNav(select: Boolean = false) {
        if (select) {
            binding.ivMessage.startAnimation(zoomAnimation)
            binding.tvMessage.setTextColor(getColor(R.color.FF8E58FB))
            binding.ivMessage.setImageResource(R.drawable.main_nav_message_select)
        } else {
            binding.ivMessage.clearAnimation()
            binding.tvMessage.setTextColor(getColor(R.color.FF5B5D66))
            binding.ivMessage.setImageResource(R.drawable.main_nav_message)
        }
    }

    private fun updateMineNav(select: Boolean = false) {
        if (select) {
            FireBaseManager.logEvent(FirebaseKey.CLICK_MY_BUTTON)
            binding.ivMine.startAnimation(zoomAnimation)
            binding.tvMine.setTextColor(getColor(R.color.FF8E58FB))
            binding.ivMine.setImageResource(R.drawable.main_nav_mine_select)
        } else {
            binding.ivMine.clearAnimation()
            binding.tvMine.setTextColor(getColor(R.color.FF5B5D66))
            binding.ivMine.setImageResource(R.drawable.main_nav_mine)
        }
    }

    private fun getColor(@ColorRes resId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(resId, context.theme)
        } else {
            resources.getColor(resId)
        }
    }

    internal fun updateUnReadCount(count: Int) {
        binding.messageRedDotView.updateMessageCount(count)
    }

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        val clickEnable = currentTime - lastClickTime > clickMinTimeInterval
        when (v) {
            binding.llHome -> {
                if (clickEnable && lastSelectType == Type.Home) {
                    itemOnClickListener?.invoke(true, Type.Home)
                } else if (clickEnable || lastSelectType != Type.Home) {
                    itemOnClickListener?.invoke(false, Type.Home)
                    updateSelectNav(Type.Home)
                    lastClickTime = currentTime
                }
            }
            binding.llSquare -> {
                if (clickEnable || lastSelectType != Type.Square) {
                    itemOnClickListener?.invoke(false, Type.Square)
                    updateSelectNav(Type.Square)
                    lastClickTime = currentTime
                }
            }
            binding.clMessage -> {
                if (clickEnable || lastSelectType != Type.Message) {
                    itemOnClickListener?.invoke(false, Type.Message)
                    updateSelectNav(Type.Message)
                    lastClickTime = currentTime
                }
            }
            binding.llMine -> {
                if (clickEnable || lastSelectType != Type.Mine) {
                    itemOnClickListener?.invoke(false, Type.Mine)
                    updateSelectNav(Type.Mine)
                    lastClickTime = currentTime
                }
            }
        }
    }

    enum class Type { Home, Message, Square, Mine }
}