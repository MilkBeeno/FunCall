package com.milk.funcall.account.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.app.AppConfig
import com.milk.simple.ktx.ioScope

class RechargeViewModel : ViewModel() {
    internal fun salesOrder(productId: String, purchaseToken: String) {
        ioScope { AppConfig.getSubscribeStatus(productId, purchaseToken) }
    }
}