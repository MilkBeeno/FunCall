package com.milk.common.paging

abstract class MultiTypeDelegate {
    abstract fun getItemViewId(viewType: Int):Int
}