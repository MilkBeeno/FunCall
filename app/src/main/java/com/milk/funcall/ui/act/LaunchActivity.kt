package com.milk.funcall.ui.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.milk.funcall.databinding.ActivityLaunchBinding
import com.milk.ktx.viewBinding

class LaunchActivity : AppCompatActivity() {
    private val binding by viewBinding<ActivityLaunchBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}