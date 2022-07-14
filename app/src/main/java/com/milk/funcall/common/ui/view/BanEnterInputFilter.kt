package com.milk.funcall.common.ui.view

import android.text.InputFilter
import android.text.Spanned

class BanEnterInputFilter : InputFilter {
    override fun filter(
        p0: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Spanned?,
        p4: Int,
        p5: Int
    ): CharSequence {
        return p0.toString().replace("\n", "")
    }
}