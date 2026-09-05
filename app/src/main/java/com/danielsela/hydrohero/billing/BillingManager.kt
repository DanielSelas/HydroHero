package com.danielsela.hydrohero.billing

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

/**
 * Product ids as configured in Play Console > Monetize > Products.
 * These must match exactly or the store returns an empty product list.
 */
object BillingProducts {
    const val PREMIUM_MONTHLY = "hydrohero_premium_monthly"
    const val PREMIUM_LIFETIME = "hydrohero_premium_lifetime"
}

/** What the user is entitled to, derived purely from what Play reports. */
data class Entitlement(
    val isPremium: Boolean,
    val premiumType: String,
) {
    companion object {
        val NONE = Entitlement(isPremium = false, premiumType = "none")
    }
}

/**
 * Owns the Play Billing connection.
 *
 * Play is the single source of truth for premium: entitlement is never set by
 * the UI, only recomputed from the purchases Play reports back. That means a
 * refund, an expiry or a cancellation flows through on the next
 * [queryPurchases] without any local bookkeeping.
 */
class BillingManager(
    context: Context,
    private val onEntitlementChanged: (Entitlement) -> Unit,
    private val onError: (String) -> Unit = {},
) : PurchasesUpdatedListener {

    private val appContext = context.applicationContext

    /** Localized prices from Play; null until the query returns. */
    var monthlyPrice by mutableStateOf<String?>(null)
        private set
    var lifetimePrice by mutableStateOf<String?>(null)
        private set

    private var productDetails = mapOf<String, ProductDetails>()
    private var isConnected = false
    private var reconnectAttempts = 0

    private val billingClient: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    fun start() {
        if (billingClient.isReady) {
            onConnected()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isConnected = true
                    reconnectAttempts = 0
                    onConnected()
                } else {
                    isConnected = false
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
                // Play services can drop the binding; retry a bounded number of
                // times rather than leaving the store permanently unavailable.
                if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    reconnectAttempts++
                    start()
                }
            }
        })
    }

    fun stop() {
        if (billingClient.isReady) billingClient.endConnection()
        isConnected = false
    }

    private fun onConnected() {
        queryProductDetails()
        queryPurchases()
    }

    /** Fetches localized prices so the paywall never hard-codes currency. */
    private fun queryProductDetails() {
        queryProductDetailsFor(
            productId = BillingProducts.PREMIUM_MONTHLY,
            productType = BillingClient.ProductType.SUBS
        )
        queryProductDetailsFor(
            productId = BillingProducts.PREMIUM_LIFETIME,
            productType = BillingClient.ProductType.INAPP
        )
    }

    private fun queryProductDetailsFor(productId: String, productType: String) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result, details ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryProductDetailsAsync
            val found = details.firstOrNull() ?: return@queryProductDetailsAsync
            productDetails = productDetails + (productId to found)

            when (productId) {
                BillingProducts.PREMIUM_LIFETIME ->
                    lifetimePrice = found.oneTimePurchaseOfferDetails?.formattedPrice
                BillingProducts.PREMIUM_MONTHLY ->
                    monthlyPrice = found.subscriptionOfferDetails
                        ?.firstOrNull()
                        ?.pricingPhases
                        ?.pricingPhaseList
                        ?.firstOrNull()
                        ?.formattedPrice
            }
        }
    }

    /**
     * Recomputes entitlement from Play. Safe to call often — call it on every
     * resume so a purchase or cancellation made elsewhere is picked up.
     */
    fun queryPurchases() {
        if (!billingClient.isReady) return

        val collected = mutableListOf<Purchase>()
        var pending = 2

        fun finish() {
            pending--
            if (pending == 0) {
                collected.forEach(::acknowledgeIfNeeded)
                onEntitlementChanged(entitlementFrom(collected))
            }
        }

        listOf(BillingClient.ProductType.INAPP, BillingClient.ProductType.SUBS).forEach { type ->
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(type).build()
            ) { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    collected += purchases
                }
                finish()
            }
        }
    }

    /**
     * @param premiumType "monthly" or "lifetime", matching the paywall buttons.
     */
    fun launchPurchase(activity: Activity, premiumType: String) {
        val productId = when (premiumType) {
            "monthly" -> BillingProducts.PREMIUM_MONTHLY
            "lifetime" -> BillingProducts.PREMIUM_LIFETIME
            else -> return
        }

        val details = productDetails[productId]
        if (details == null) {
            // Almost always means the product is not active in Play Console, the
            // build is not on a release track, or the account is not a tester.
            onError("Store is unavailable right now. Please try again later.")
            return
        }

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .apply {
                // Subscriptions must name the offer being bought; one-time products must not.
                details.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let {
                    setOfferToken(it)
                }
            }
            .build()

        val result = billingClient.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productParams))
                .build()
        )

        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            onError("Could not open checkout. Please try again.")
        }
    }

    /**
     * Subscriptions can only be cancelled by the user in Play, never by the app,
     * so this opens the managed subscription screen.
     */
    fun openSubscriptionManagement(activity: Activity) {
        val uri = Uri.parse(
            "https://play.google.com/store/account/subscriptions" +
                "?sku=${BillingProducts.PREMIUM_MONTHLY}&package=${activity.packageName}"
        )
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach(::acknowledgeIfNeeded)
                // Re-query rather than trusting this callback alone, so the
                // entitlement always reflects the full set of purchases.
                queryPurchases()
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> Unit
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> queryPurchases()
            else -> onError("Purchase failed. Please try again.")
        }
    }

    private fun entitlementFrom(purchases: List<Purchase>): Entitlement {
        val active = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }

        // Lifetime wins: someone who owns both should not be downgraded when the
        // subscription lapses.
        return when {
            active.any { BillingProducts.PREMIUM_LIFETIME in it.products } ->
                Entitlement(isPremium = true, premiumType = "lifetime")
            active.any { BillingProducts.PREMIUM_MONTHLY in it.products } ->
                Entitlement(isPremium = true, premiumType = "monthly")
            else -> Entitlement.NONE
        }
    }

    /**
     * Play automatically refunds anything not acknowledged within three days,
     * so this has to run for every purchased item we see.
     */
    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) return

        billingClient.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        ) { /* A failed ack is retried on the next queryPurchases(). */ }
    }

    private companion object {
        const val MAX_RECONNECT_ATTEMPTS = 3
    }
}
