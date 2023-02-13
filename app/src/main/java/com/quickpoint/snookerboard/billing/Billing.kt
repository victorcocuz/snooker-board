package com.quickpoint.snookerboard.billing

import android.app.Activity
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchaseState
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.quickpoint.snookerboard.utils.Constants
import com.quickpoint.snookerboard.utils.Constants.PRODUCT_BEER
import com.quickpoint.snookerboard.utils.Constants.PRODUCT_COFFEE
import com.quickpoint.snookerboard.utils.Constants.PRODUCT_LUNCH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class PurchaseHelper(val activity: Activity) {

    // Variables
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var billingClient: BillingClient
    private lateinit var productDetailsList: List<ProductDetails>
    private lateinit var purchase: Purchase

    // State Flows
    private val _productName = MutableStateFlow("Searching...")
    val productName = _productName.asStateFlow()

    private val _buyEnabled = MutableStateFlow(false)
    val buyEnabled = _buyEnabled.asStateFlow()

    private val _consumeEnabled = MutableStateFlow(false)
    val consumeEnabled = _consumeEnabled.asStateFlow()

    private val _statusText = MutableStateFlow("Initializing...")
    val statusText = _statusText.asStateFlow()

    private val _priceText = MutableStateFlow(List(3) { Constants.EMPTY_STRING })
    val priceText = _priceText.asStateFlow()

    fun billingSetup() {
        Timber.i("billingSetup()")
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        connectToGooglePlay()
    }

    private fun connectToGooglePlay() {
        Timber.i("connectToGooglePlay()")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _statusText.value = "Connected to Google Play"
                    queryProducts()
                    reloadPurchase()
                } else {
                    _statusText.value = "Billing Client Connection Failed"
                }
            }

            override fun onBillingServiceDisconnected() {
                _statusText.value = "Billing Client Connection Lost. Reconnecting..."
                connectToGooglePlay()
            }
        })
    }

    fun queryProducts() {
        Timber.i("process Purchases")
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_COFFEE)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_BEER)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_LUNCH)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        coroutineScope.launch {
            billingClient.queryProductDetails(queryProductDetailsParams).productDetailsList?.let { list ->
                productDetailsList = list
                val price = MutableList(3) {Constants.EMPTY_STRING}
                list.forEach {
                    when (it.productId) {
                        PRODUCT_COFFEE -> price[0] = it.oneTimePurchaseOfferDetails?.formattedPrice ?: Constants.EMPTY_STRING
                            PRODUCT_BEER -> price[1] = it.oneTimePurchaseOfferDetails?.formattedPrice ?: Constants.EMPTY_STRING
                        else -> price[2] = it.oneTimePurchaseOfferDetails?.formattedPrice ?: Constants.EMPTY_STRING
                    }
                }
                _priceText.value = price
                _statusText.value = "Products cueried"
            }
        }
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                completePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _statusText.value = "Purchase Canceled"
        } else {
            _statusText.value = "Purchase Error: ${billingResult.responseCode} could not be processed"
        }
    }

    private fun completePurchase(item: Purchase) {
        purchase = item
        if (purchase.purchaseState == PurchaseState.PURCHASED) {
            _buyEnabled.value = false
            _consumeEnabled.value = true
            _statusText.value = "Purchase Completed"
            consumePurchase()
        }
    }

    fun makePurchase(purchaseId: String) {
        Timber.i("makePurchase()")
        val productDetails = productDetailsList[when (purchaseId) {
            PRODUCT_COFFEE -> 0
            PRODUCT_BEER -> 1
            else -> 2
        }]

        val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
        ).build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            makePurchase(purchaseId)
        }
    }


    private fun consumePurchase() {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        when (purchase.purchaseState) {
            PurchaseState.PURCHASED -> {
                coroutineScope.launch {
                    val result = billingClient.consumePurchase(consumeParams)
                    if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _statusText.value = "Purchase Consumed"
                        _buyEnabled.value = true
                        _consumeEnabled.value = false
                    }
                    _statusText.value = "Thank you"
                }
            }
            PurchaseState.PENDING -> _statusText.value = "Transaction is pending"
            else -> _statusText.value = "No implementation for purchase state ${purchase.purchaseState}"
        }
    }

    fun reloadPurchase() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params, purchasesListener)
    }

    private val purchasesListener =
        PurchasesResponseListener { billingResult, purchases ->
            if (purchases.isNotEmpty()) {
                purchase = purchases.first()
                _buyEnabled.value = false
                _consumeEnabled.value = true
                _statusText.value = "Previous Purchase Found"
            } else {
                _buyEnabled.value = true
                _consumeEnabled.value = false
            }
        }
}