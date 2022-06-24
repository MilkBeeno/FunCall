package com.milk.funcall.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.milk.simple.listener.MultipleClickListener

abstract class AbstractFragment : Fragment(), View.OnClickListener {
    private var isLoaded = false
    private var isAddToFragment = false

    private val multipleClickListener by lazy {
        object : MultipleClickListener() {
            override fun onMultipleClick(view: View) =
                this@AbstractFragment.onMultipleClick(view)
        }
    }

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
        initializeObserver()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isAddToFragment && !isLoaded) {
            isLoaded = true
            initializeView()
        }
    }

    override fun onClick(p0: View) = multipleClickListener.onMultipleClick(p0)

    abstract fun getRootView(): View
    abstract fun initializeView()
    protected open fun initializeData() {}
    protected open fun initializeObserver() {}
    protected open fun onMultipleClick(view: View) = Unit
}