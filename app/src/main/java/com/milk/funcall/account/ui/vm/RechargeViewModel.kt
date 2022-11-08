package com.milk.funcall.account.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.app.AppConfig

class RechargeViewModel : ViewModel() {
    internal fun salesOrder(productId: String, purchaseToken: String) {
        AppConfig.getSubscribeStatus(productId, purchaseToken)
    }
}