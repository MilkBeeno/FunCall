package com.milk.funcall.main.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityBlackedListBinding

class BlackedListActivity : AbstractActivity() {
    private val binding by lazy { ActivityBlackedListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, BlackedListActivity::class.java))
        }
    }
}