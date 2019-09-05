package com.sanskritbasics

import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import com.android.billingclient.api.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity :AppCompatActivity(),PurchasesUpdatedListener {

	private lateinit var billingClient    :BillingClient
	private lateinit var itemsDescription :MutableList<SkuDetails>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.supportActionBar?.hide()
		setContentView(R.layout.activity_about)

		textProjectUrl.movementMethod = LinkMovementMethod.getInstance()
		textPlayMarket.movementMethod = LinkMovementMethod.getInstance()
		textFDroid    .movementMethod = LinkMovementMethod.getInstance()

		val info :PackageInfo? = packageManager.getPackageInfo(packageName, 0)
		@Suppress("DEPRECATION") val version = "${info?.versionName} (${info?.versionCode})"
		textVersion.text = version

		billingClient = BillingClient.newBuilder(applicationContext).setListener(this).enablePendingPurchases().build()
		billingClient.startConnection(object :BillingClientStateListener {
			override fun onBillingSetupFinished(billingResult: BillingResult) {
				if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
					val skuList = arrayListOf("dn01", "dn02", "dn03", "dn05", "dn10", "dn15", "dn20")
					val params = SkuDetailsParams.newBuilder()
					params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
					billingClient.querySkuDetailsAsync(params.build()) { result, skuDetailsList ->
						Log.d("!!!!!!!!!!!!!!!", "Result is ${result.responseCode}")
						if (result.responseCode == BillingClient.BillingResponseCode.OK) {
							Log.d("!!!!!!!!!!!!!!!", "Number in List: ${skuDetailsList.size}")
							skuDetailsList.sortWith(compareBy { it.sku })
							itemsDescription = skuDetailsList
							spinnerItems.adapter = AboutSkuDetailsAdapter(applicationContext, itemsDescription)
						}
					}
				} else {
					buttonBuy.isEnabled = false
					spinnerItems.isEnabled = false
					billing.visibility = View.GONE
					Snackbar.make(textFDroid, R.string.about_services_unavailable, Snackbar.LENGTH_LONG).show()
				}
			}
			override fun onBillingServiceDisconnected() {
				Snackbar.make(textFDroid, R.string.about_services_disconnected, Snackbar.LENGTH_LONG).show()
			}
		})

		buttonBuy.setOnClickListener {
			val detail = (spinnerItems.adapter as AboutSkuDetailsAdapter).getSkuDetails(spinnerItems.selectedItemPosition)
			detail?.let {
				val flowParams = BillingFlowParams.newBuilder()
					.setSkuDetails(it)
					.build()
				billingClient.launchBillingFlow(this, flowParams)
			}
		}
	}

	override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
		when(billingResult?.responseCode) {
			BillingClient.BillingResponseCode.OK -> {
				purchases?.let {
					for (purchase in purchases) {
						if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
							val param = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
							billingClient.consumeAsync(param) { billingResultAsync, _ ->
								if (billingResultAsync.responseCode == BillingClient.BillingResponseCode.OK) {
									Snackbar.make(textFDroid, R.string.about_order_successful, Snackbar.LENGTH_LONG).show()
									Log.d("BILLING", "CONSUMED SUCCESSFULLY")
								}
								Log.d("BILLING", "Unsuccessful result code")
							}
						}
					}
				}
			}
			BillingClient.BillingResponseCode.USER_CANCELED -> {
				Snackbar.make(textFDroid, R.string.about_order_cancelled, Snackbar.LENGTH_LONG).show()
				Log.d("BILLING", "order cancelled")
			}
			BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
				Snackbar.make(textFDroid, R.string.about_order_pending, Snackbar.LENGTH_LONG).show()
				Log.d("BILLING", "Item order is in pending state")
			}
			BillingClient.BillingResponseCode.ERROR -> {
				Snackbar.make(textFDroid, R.string.about_order_failed, Snackbar.LENGTH_LONG).show()
				Log.d("BILLING", "order unsuccessful")
			}
			else -> { Log.d("BILLING", "SOME ERROR OCCURRED: ${billingResult?.responseCode}") }
		}
	}
}
