package com.milk.funcall.user.ui.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.milk.funcall.databinding.ActivityUserInfoBinding
import com.milk.simple.ktx.viewBinding

class UserInfoActivity : AppCompatActivity() {
    private val binding by viewBinding<ActivityUserInfoBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}