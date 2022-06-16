package com.milk.funcall.common.ui

import android.content.res.Resources
import android.os.Looper
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import me.jessyan.autosize.AutoSizeCompat

abstract class AbstractActivity : FragmentActivity() {
    override fun getResources(): Resources {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
        }
        return super.getResources()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}