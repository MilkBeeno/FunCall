package com.milk.funcall.user.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.milk.funcall.databinding.ActivityUserInfoBinding
import com.milk.funcall.user.data.UserTotalInfoModel
import com.milk.simple.ktx.viewBinding

class UserTotalInfoActivity : AppCompatActivity() {
    private val binding by viewBinding<ActivityUserInfoBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        private const val USER_ID = "USER_ID"
        private const val USER_TOTAL_INFO = "USER_TOTAL_INFO"
        fun create(context: Context, userId: Long = 0, user: UserTotalInfoModel? = null) {
            val intent = Intent(context, UserTotalInfoActivity::class.java)
            intent.putExtra(USER_ID, userId)
            intent.putExtra(USER_TOTAL_INFO, user)
            context.startActivity(intent)
        }
    }
}