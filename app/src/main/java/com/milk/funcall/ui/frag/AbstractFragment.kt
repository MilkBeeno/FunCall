package com.milk.funcall.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class AbstractFragment : Fragment() {
    private var isLoaded = false
    private var isAddToFragment = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isAddToFragment = true
        return getContentView()
    }

    abstract fun getContentView(): View

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAddToFragment && !isLoaded) {
            isLoaded = true
            initializeView()
        }
    }

    abstract fun initializeView()
}