package com.quickpoint.snookerboard.billing

import android.app.Activity
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchaseState
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.SnookerBoardApplication
import com.quickpoint.snookerboard.databinding.FragmentNavDonateBinding
import com.quickpoint.snookerboard.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object Billing {

    private lateinit var billingClient: BillingClient

    fun initBilling(purchasesUpdatedListener: PurchasesUpdatedListener) {
        billingClient =
            BillingClient.newBuilder(SnookerBoardApplication.application())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()
        connectToGooglePlay()
    }

    private fun connectToGooglePlay() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Timber.i("Connected to Google Play")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request by calling the startConnection() method.
                connectToGooglePlay()
            }
        })
    }

    suspend fun processPurchases(activity: Activity, binding: FragmentNavDonateBinding) {
        Timber.i("process Purchases")
        val productList = listOf(
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
                .build())

        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        // leverage queryProductDetails Kotlin extension function to get productDetailsResult
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }
        productDetailsResult.productDetailsList?.let { processDetailsResult(activity, binding, it) }
    }

    private fun processDetailsResult(activity: Activity, binding: FragmentNavDonateBinding, productDetailsList: List<ProductDetails>) {
        Timber.i("processDetailResult()")
        for (productDetails in productDetailsList) {
            Timber.i("productDetails id: ${productDetails.productId}, price: ${productDetails.oneTimePurchaseOfferDetails?.formattedPrice}")
            displayPaymentInfo(activity, productDetails, when (productDetails.productId) {
                PRODUCT_COFFEE -> binding.fNavDonateLlCoffee
                PRODUCT_BEER -> binding.fNavDonateLlBeer
                else -> binding.fNavDonateLlLunch
            })
        }
    }

    private fun displayPaymentInfo(activity: Activity, productDetails: ProductDetails, linearLayout: LinearLayout) {
        linearLayout.setOnClickListener { launchPurchaseFlow(activity, productDetails) }
        (linearLayout.getChildAt(2) as TextView).text = productDetails.oneTimePurchaseOfferDetails?.formattedPrice
    }

    private fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        Timber.i("launchPurchaseFlow()")
        // val selectedOfferIndex = 0
        // val selectedOfferToken = productDetails.subscriptionOfferDetails?.get(selectedOfferIndex)?.offerToken ?: return
        val productDetailsParamsList = listOf(BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            // to get an offer token, call ProductDetails.subscriptionOfferDetails()
            // for a list of offers that are available to the user
            // .setOfferToken(selectedOfferToken)
            .build())
        val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build()

        // Launch the billing flow
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            launchPurchaseFlow(activity, productDetails)
        }
    }


    suspend fun handlePurchase(purchase: Purchase, context: Context) { // Verify purchases
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        Timber.i("handlePurchase() with token: ${purchase.purchaseToken}")
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        when (purchase.purchaseState) {
            PurchaseState.PURCHASED -> {
                billingClient.consumePurchase(consumeParams)
                context.activity()!!.toast(context.getString(R.string.snackbar_donation))
            }
            PurchaseState.PENDING -> Timber.e("Transaction is pending")
            else -> Timber.e("No implementation for purchase state ${purchase.purchaseState}")
        }
    }

    suspend fun queryPurchasesAsync(context: Context) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)

        val purchasesResult = billingClient.queryPurchasesAsync(params.build())
        if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            for (purchase in purchasesResult.purchasesList) {
                if (purchase.purchaseState == PurchaseState.PURCHASED && !purchase.isAcknowledged)
                    handlePurchase(purchase, context)
            }
        }
    }
}