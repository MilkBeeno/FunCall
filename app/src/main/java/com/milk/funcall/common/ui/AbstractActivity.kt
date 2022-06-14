package com.milk.funcall.common.ui

import android.content.res.Resources
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import me.jessyan.autosize.AutoSizeCompat

abstract class AbstractActivity : FragmentActivity() {
    override fun getResources(): Resources {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
        }
        return super.getResources()
    }
}