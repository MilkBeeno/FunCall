package com.milk.funcall.common.pay

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.collection.arrayMapOf
import com.android.billingclient.api.*
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

class GooglePlay : Pay {
    private var billingClient: BillingClient? = null
    private val currencyArrayMap = arrayMapOf<String, String>()
    private var paySuccessListener: ((String, String) -> Unit)? = null
    private var payCancelListener: (() -> Unit)? = null
    private var payFailureListener: (() -> Unit)? = null
    internal val productList = MutableSharedFlow<MutableList<ProductsModel>>()

    /** 谷歌支付连接状态回调 */
    private val billingClientStateListener by lazy {
        object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Logger.d("谷歌支付连接失败", "GooglePlay")
                disconnect()
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    Logger.d("谷歌支付连接成功", "GooglePlay")
                    connected()
                }
            }
        }
    }

    /** 内购或订阅产品购买回调 */
    private val purchasesUpdatedListener by lazy {
        PurchasesUpdatedListener { p0, purchases ->
            when (p0.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    purchases?.forEach {
                        Logger.d("谷歌商品订阅成功", "GooglePlay")
                        //不可重复购买的内购商品、订阅商品核销
                        acknowledgedPurchase(it.orderId, it.purchaseToken)
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    Logger.d("谷歌商品取消订阅", "GooglePlay")
                    payCancelListener?.invoke()
                }
                else -> {
                    Logger.d("谷歌商品订阅失败", "GooglePlay")
                    payFailureListener?.invoke()
                }
            }
        }
    }

    /***
     * 收集到了一个 ANR 问题，从信息看是来自于 onBillingServiceDisconnected，查询了一下， 有人也遇到了相同的问题，
     * 判断是由于手机环境的问题导致 onBillingServiceDisconnected 不断回调，startConnection 不断调用引起，
     * 所以我们可以给 startConnection 加个间隔时间
     */
    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    private val runnable = Runnable { billingClient?.startConnection(billingClientStateListener) }

    override fun initialize(activity: Activity) {
        if (billingClient == null) {
            val builder = BillingClient.newBuilder(activity)
            builder.setListener(purchasesUpdatedListener)
            builder.enablePendingPurchases()
            billingClient = builder.build()
        }
        billingClient?.startConnection(billingClientStateListener)
    }

    /** 谷歌支付断开连接时重新进行连接 */
    override fun disconnect() {
        handler.postDelayed(runnable, 1000)
    }

    override fun connected() {
        val subsParams = getSuBsProductDetailsParams()
        // 查询谷歌支持内购或订阅产品详细回调
        val responseListener = ProductDetailsResponseListener { _, productDetails ->
            Logger.d("谷歌订阅查询成功", "GooglePlay")
            // 获取谷歌内购或订阅产品价格、并进行货币转换
            getProductPrice(productDetails)
        }
        billingClient?.queryProductDetailsAsync(subsParams, responseListener)
    }

    /** 查询谷歌订阅产品参数配置 */
    private fun getSuBsProductDetailsParams(): QueryProductDetailsParams {
        val subscriptionProductInfo = arrayListOf<QueryProductDetailsParams.Product>()
        subscriptionProductInfo.add(
            QueryProductDetailsParams.Product.newBuilder().setProductId("productId")
                .setProductType(BillingClient.ProductType.SUBS).build()
        )
        return QueryProductDetailsParams.newBuilder().setProductList(subscriptionProductInfo)
            .build()
    }

    /** 获取谷歌内购或订阅产品价格、并进行货币转换 */
    private fun getProductPrice(productDetails: MutableList<ProductDetails>) {
        Logger.d("当前查询1", "GooglePlay")
        if (currencyArrayMap.isEmpty) {
            // currencyCode 为 key ,货币符号为 value 对于没有特定符号的货币，symbol 与 currencyCode 相同
            Currency.getAvailableCurrencies().forEach {
                currencyArrayMap[it.currencyCode] = it.symbol
            }
        }
        Logger.d("当前查询2", "GooglePlay")
        val products = mutableListOf<ProductsModel>()
        productDetails.forEach {
            when {
                BillingClient.ProductType.INAPP == it.productType && it.oneTimePurchaseOfferDetails != null -> {
                    Logger.d("当前内购商品，INAPP 内购", "GooglePlay")
                    val googleProductPrice =
                        it.oneTimePurchaseOfferDetails?.formattedPrice.toString()
                    val googleCurrencyCode =
                        it.oneTimePurchaseOfferDetails?.priceCurrencyCode.toString()
                    val currencySymbol = currencyArrayMap[googleCurrencyCode] ?: googleCurrencyCode
                    val replacePrice = replaceCurrencySymbol(
                        googleProductPrice, googleCurrencyCode, currencySymbol
                    )
                    Logger.d("当前订阅商品价格是:$replacePrice，INAPP 内购", "GooglePlay")
                }
                BillingClient.ProductType.SUBS == it.productType && it.subscriptionOfferDetails != null -> {
                    Logger.d("当前订阅商品，SUBS 订阅", "GooglePlay")
                    val googleProductPrice =
                        it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString()
                    val googleCurrencyCode =
                        it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.priceCurrencyCode.toString()
                    val currencySymbol = currencyArrayMap[googleCurrencyCode] ?: googleCurrencyCode
                    val replacePrice = replaceCurrencySymbol(
                        googleProductPrice, googleCurrencyCode, currencySymbol
                    )
                    products.add(ProductsModel(it, replacePrice))
                    Logger.d("当前订阅商品价格是:$replacePrice，SUBS 订阅", "GooglePlay")
                }
            }
        }
        ioScope { productList.emit(products) }
    }

    /** 对谷歌内购或订阅产品进行货币转换 */
    private fun replaceCurrencySymbol(
        priceStr: String, currencyCode: String, currencySymbol: String?
    ): String {
        var resultPriceStr = priceStr
        if (resultPriceStr.startsWith("$")) {
            if (currencySymbol != null) {
                when {
                    // 没有货币符号的情况，把货币码拼接到前面
                    currencySymbol == currencyCode -> resultPriceStr =
                        currencySymbol + resultPriceStr
                    !resultPriceStr.startsWith(currencySymbol) -> {
                        resultPriceStr = resultPriceStr.replace("$", currencySymbol)
                    }
                }
            }
        }
        return resultPriceStr
    }

    override fun launchPurchase(activity: Activity, productDetails: Any) {
        if (productDetails is ProductDetails) {
            val params = arrayListOf<BillingFlowParams.ProductDetailsParams>()
            val isSubs = BillingClient.ProductType.SUBS == productDetails.productType
            when {
                isSubs && productDetails.subscriptionOfferDetails != null -> {
                    val offerToken =
                        productDetails.subscriptionOfferDetails?.get(0)?.offerToken.toString()
                    params.add(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails).setOfferToken(offerToken).build()
                    )
                }
                else -> params.add(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails).build()
                )
            }
            val builder = BillingFlowParams.newBuilder()
            builder.setProductDetailsParamsList(params)
            builder.setObfuscatedAccountId("")
            val billingFlowParams = builder.build()
            billingClient?.launchBillingFlow(activity, billingFlowParams)
        }
    }

    /** 谷歌商品订阅成功，进行商品核销 */
    private fun acknowledgedPurchase(orderId: String, purchaseToken: String) {
        if (billingClient != null && billingClient?.isReady == true) {
            val builder = AcknowledgePurchaseParams.newBuilder()
            builder.setPurchaseToken(purchaseToken)
            val acknowledgePurchaseParams = builder.build()
            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) {
                Logger.d("谷歌商品订阅核销成功", "GooglePlay")
                paySuccessListener?.invoke(orderId, purchaseToken)
            }
        }
    }

    override fun paySuccessListener(listener: (String, String) -> Unit) {
        paySuccessListener = listener
    }

    override fun payCancelListener(listener: () -> Unit) {
        payCancelListener = listener
    }

    override fun payFailureListener(listener: () -> Unit) {
        payFailureListener = listener
    }

    override fun finishConnection() {
        try {
            billingClient?.endConnection()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}