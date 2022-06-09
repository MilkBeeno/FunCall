package com.milk.funcall.ui.act

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import me.jessyan.autosize.AutoSizeCompat

abstract class AbstractActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeWindowConfig()
    }

    private fun initializeWindowConfig() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.statusBarColor = Color.TRANSPARENT
    }

    override fun getResources(): Resources {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
        }
        return super.getResources()
    }
}