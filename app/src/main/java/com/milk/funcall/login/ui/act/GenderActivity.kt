package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.milk.funcall.R
import com.milk.funcall.common.emun.Gender
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityGenderBinding
import com.milk.simple.ktx.color
import com.milk.simple.ktx.immersiveStatusBar

class GenderActivity : AbstractActivity() {
    private val binding by lazy { ActivityGenderBinding.inflate(layoutInflater) }
    private var selectGender = Gender.Man

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersiveStatusBar()
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        updateManStatus(true)
        updateWomanStatus(false)
        binding.clMan.setOnClickListener {
            if (selectGender != Gender.Man) {
                updateManStatus(true)
                updateWomanStatus(false)
                selectGender = Gender.Man
            }
        }
        binding.clWoman.setOnClickListener {
            if (selectGender != Gender.Woman) {
                updateManStatus(false)
                updateWomanStatus(true)
                selectGender = Gender.Woman
            }
        }
        binding.tvGenderNext.setOnClickListener {
            LoginActivity.create(this, selectGender)
            finish()
        }
    }

    private fun updateManStatus(select: Boolean) {
        binding.clMan.setBackgroundResource(
            if (select)
                R.drawable.shape_gender_man_select_background
            else
                R.drawable.shape_gender_select_background
        )
        binding.tvManFirst.setTextColor(
            if (select) color(R.color.white) else color(R.color.FF1E1E21)
        )
        binding.tvManSecond.setTextColor(
            if (select) color(R.color.white) else color(R.color.FF8E58FB)
        )
        binding.tvManSecond.textSize = if (select) 29f else 14f
        val params = binding.tvManSecond.layoutParams as ConstraintLayout.LayoutParams
        params.bottomMargin = if (select) 8 else 0
        binding.tvManSecond.layoutParams = params
    }

    private fun updateWomanStatus(select: Boolean) {
        binding.clWoman.setBackgroundResource(
            if (select)
                R.drawable.shape_gender_woman_select_background
            else
                R.drawable.shape_gender_select_background
        )
        binding.tvWomanFirst.setTextColor(
            if (select) color(R.color.white) else color(R.color.FF1E1E21)
        )
        binding.tvWomanSecond.setTextColor(
            if (select) color(R.color.white) else color(R.color.FFFA64C8)
        )
        binding.tvWomanSecond.textSize = if (select) 29f else 14f
        val params = binding.tvWomanSecond.layoutParams as ConstraintLayout.LayoutParams
        params.bottomMargin = if (select) 8 else 0
        binding.tvWomanSecond.layoutParams = params
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, GenderActivity::class.java))
    }
}