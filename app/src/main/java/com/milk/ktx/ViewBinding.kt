package com.milk.ktx

import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

@MainThread
inline fun <reified VB : ViewBinding> ComponentActivity.viewBinding(): Lazy<VB> {
    return ViewBindingLazy(VB::class, this)
}

class ViewBindingLazy<VB : ViewBinding>(
    private val viewModelClass: KClass<VB>,
    private val context: ComponentActivity
) : Lazy<VB> {

    private var cached: VB? = null
    override val value: VB
        get() {
            return cached ?: (viewModelClass.java
                .getMethod("inflate", LayoutInflater::class.java)
                .invoke(null, context.layoutInflater) as VB)
                .also { cached = it }
        }

    override fun isInitialized(): Boolean = cached != null
}
