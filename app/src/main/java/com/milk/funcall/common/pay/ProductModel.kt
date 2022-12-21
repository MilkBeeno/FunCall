package com.milk.funcall.common.pay

import com.android.billingclient.api.ProductDetails

data class ProductModel(
    // 从谷歌商店中查询的商品信息
    var productDetails: ProductDetails? = null,
    // 转换后商品的价格、按照本地货币进行格式化过的价格
    var productPrice: String = ""
)