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
        return getRootView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAddToFragment && !isLoaded) {
            isLoaded = true
            initializeView()
        }
    }

    abstract fun getRootView(): View
    abstract fun initializeView()
    protected open fun initializeData() {}
}