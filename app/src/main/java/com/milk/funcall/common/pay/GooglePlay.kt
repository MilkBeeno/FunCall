package com.milk.funcall.common.pay

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.collection.arrayMapOf
import com.android.billingclient.api.*
import com.milk.funcall.common.constrant.AppConfigKey
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

class GooglePlay : Pay {
    private var billingClient: BillingClient? = null

    /** 查询商品的 ID */
    private val productIds = mutableListOf<String>()

    /** 货币符号转换对应地区和汇率 */
    private val currencyArrayMap = arrayMapOf<String, String>()

    /** 查询到的商品列表 */
    internal val products = MutableSharedFlow<MutableMap<String, ProductsModel>>()

    /** 支付结果状态回调 */
    private var payFailed: (() -> Unit)? = null
    private var payCanceled: (() -> Unit)? = null
    private var paySucceeded: ((String, String) -> Unit)? = null

    /** 谷歌支付连接状态回调 */
    private val billingClientStateListener by lazy {
        object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Logger.d("谷歌支付连接失败", TAG)
                connectFailed()
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    Logger.d("谷歌支付连接成功", TAG)
                    connectSucceeded()
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
                        Logger.d("谷歌商品购买成功", TAG)
                        // 不可重复购买的内购商品、订阅商品核销
                        acknowledgedPurchase(it.orderId, it.purchaseToken)
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    Logger.d("谷歌商品取消购买", TAG)
                    payCanceled?.invoke()
                }
                else -> {
                    Logger.d("谷歌商品购买失败，Code = ${p0.responseCode}", TAG)
                    payFailed?.invoke()
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

    override fun initialize(context: Context) {
        if (billingClient == null) {
            val builder = BillingClient.newBuilder(context)
            builder.setListener(purchasesUpdatedListener)
            builder.enablePendingPurchases()
            billingClient = builder.build()
        }
        billingClient?.startConnection(billingClientStateListener)
    }

    override fun connectFailed() {
        handler.postDelayed(runnable, 1000)
    }

    override fun connectSucceeded() {
        // 连接成功、Google Play 直接去查商品得状态
        queryProducts()
    }

    override fun disconnect() {
        billingClient?.endConnection()
    }

    override fun queryProducts() {
        val subsParams = getSubsProductDetailsParams()
        // 查询谷歌支持内购或订阅产品详细回调
        val responseListener = ProductDetailsResponseListener { billingResult, productDetails ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                ioScope {
                    writeOffAnOrder()
                    Logger.d("谷歌商品查询成功", TAG)
                    // 获取谷歌内购或订阅产品价格、并进行货币转换
                    getProductPrice(productDetails)
                }
            } else {
                Logger.d(
                    "谷歌商品查询失败," + "查询的 Code 是=${billingResult.responseCode}",
                    TAG
                )
            }
        }
        billingClient?.queryProductDetailsAsync(subsParams, responseListener)
    }

    /** 进入充值页面、核销上次未核销的订单 */
    private suspend fun writeOffAnOrder() {
        val params =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        val result = billingClient?.queryPurchasesAsync(params)
        val purchasesList = result?.purchasesList
        purchasesList?.forEach {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged) {
                acknowledgedPurchase(it.orderId, it.purchaseToken)
            }
        }
    }

    /** 查询谷歌订阅产品参数配置 */
    private fun getSubsProductDetailsParams(): QueryProductDetailsParams {
        val subscriptionProductInfo = arrayListOf<QueryProductDetailsParams.Product>()
        productIds.forEach {
            subscriptionProductInfo.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
        }
        Logger.d("查询产品ID的长度是: ${productIds.size}", TAG)
        return QueryProductDetailsParams.newBuilder()
            .setProductList(subscriptionProductInfo)
            .build()
    }

    /** 获取谷歌内购或订阅产品价格、并进行货币转换 */
    private suspend fun getProductPrice(productDetails: MutableList<ProductDetails>) {
        if (currencyArrayMap.isEmpty) {
            // currencyCode 为 key ,货币符号为 value 对于没有特定符号的货币，symbol 与 currencyCode 相同
            Currency.getAvailableCurrencies().forEach {
                currencyArrayMap[it.currencyCode] = it.symbol
            }
        }
        val products = mutableMapOf<String, ProductsModel>()
        Logger.d("查询当前商品列表信息，长度是${productDetails.size}", TAG)
        productDetails.forEach {
            when {
                BillingClient.ProductType.INAPP == it.productType && it.oneTimePurchaseOfferDetails != null -> {
                    Logger.d("当前内购商品，INAPP 内购", TAG)
                    val googleProductPrice =
                        it.oneTimePurchaseOfferDetails?.formattedPrice.toString()
                    val googleCurrencyCode =
                        it.oneTimePurchaseOfferDetails?.priceCurrencyCode.toString()
                    val currencySymbol = currencyArrayMap[googleCurrencyCode] ?: googleCurrencyCode
                    val replacePrice = replaceCurrencySymbol(
                        googleProductPrice, googleCurrencyCode, currencySymbol
                    )
                    Logger.d("当前内购商品价格是:$replacePrice，INAPP 内购", TAG)
                }
                BillingClient.ProductType.SUBS == it.productType && it.subscriptionOfferDetails != null -> {
                    Logger.d("当前订阅商品，SUBS 订阅", TAG)
                    val googleProductPrice =
                        it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice.toString()
                    val googleCurrencyCode =
                        it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.priceCurrencyCode.toString()
                    val currencySymbol = currencyArrayMap[googleCurrencyCode] ?: googleCurrencyCode
                    val replacePrice = replaceCurrencySymbol(
                        googleProductPrice, googleCurrencyCode, currencySymbol
                    )
                    when (it.productId) {
                        AppConfigKey.PRODUCT_ID_OF_WEEK -> {
                            products[AppConfigKey.PRODUCT_ID_OF_WEEK] =
                                ProductsModel(it, replacePrice)
                        }
                        AppConfigKey.PRODUCT_ID_OF_YEAR -> {
                            products[AppConfigKey.PRODUCT_ID_OF_YEAR] =
                                ProductsModel(it, replacePrice)
                        }
                    }
                    Logger.d("当前订阅商品详情是:$it，SUBS 订阅", TAG)
                }
            }
        }
        this.products.emit(products)
    }

    /** 对谷歌内购或订阅产品进行货币转换 */
    private fun replaceCurrencySymbol(
        priceStr: String,
        currencyCode: String,
        currencySymbol: String?
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

    override fun payProduct(activity: Activity, productDetails: Any) {
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

    override fun payFailed(request: () -> Unit) {
        payFailed = request
    }

    override fun payCanceled(request: () -> Unit) {
        payCanceled = request
    }

    override fun paySucceeded(request: (String, String) -> Unit) {
        paySucceeded = request
    }

    /** 谷歌商品订阅成功，进行商品核销 */
    private fun acknowledgedPurchase(orderId: String, purchaseToken: String) {
        if (billingClient != null && billingClient?.isReady == true) {
            val builder = AcknowledgePurchaseParams.newBuilder()
            builder.setPurchaseToken(purchaseToken)
            val acknowledgePurchaseParams = builder.build()
            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) {
                Logger.d("谷歌商品订阅核销成功", TAG)
                paySucceeded?.invoke(orderId, purchaseToken)
            }
        }
    }

    /** 当服务器接口响应后、需要先设置到此处 */
    fun addProduct(productId: String) {
        if (!productIds.contains(productId)) {
            productIds.add(productId)
        }
    }

    /** 工厂模式便于后续添加其他方式进行支付模板设计 */
    class GoogleFactory : Pay.Factory() {
        private var googlePlay: GooglePlay? = null

        override fun create(): Pay {
            if (googlePlay == null) googlePlay = GooglePlay()
            return checkNotNull(googlePlay)
        }

        companion object {
            val INSTANCE = GoogleFactory()
        }
    }

    companion object {
        private const val TAG = "谷歌 Pay"
    }
}