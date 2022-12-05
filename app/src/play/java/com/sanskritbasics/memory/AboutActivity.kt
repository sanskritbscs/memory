package com.sanskritbasics.memory

import android.content.Context
import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.coroutines.launch

class AboutActivity :AppCompatActivity(), BillingClientStateListener, PurchasesUpdatedListener {

	class AboutProductDetailsAdapter(private val context : Context, private val items :List<ProductDetails>) : BaseAdapter() {
		private val layoutInflater = LayoutInflater.from(context)

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view  = convertView ?: layoutInflater.inflate(R.layout.support_simple_spinner_dropdown_item, null)
			val text  = view.findViewById<TextView>(android.R.id.text1)
			text.text = items[position].description
			text.setTextColor(ContextCompat.getColor(context, android.R.color.black))
			text.textSize = 20f
			text.setPadding(16,8,16,8)
			text.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
//		Log.d("!!!!!", "Title: ${items[position].title}")
			return view
		}

		override fun hasStableIds() :Boolean { return true            }
		override fun isEmpty     () :Boolean { return items.isEmpty() }
		override fun getCount    () :Int     { return items.size      }
		override fun getItemId(position: Int) :Long            { return position.toLong() }
		override fun getItem  (position: Int) : ProductDetails { return items[position]    }

		fun getProductDetails(position: Int) : ProductDetails? { return if (items.isNotEmpty()) items[position] else null }
	}

	private lateinit var client  :BillingClient
	private lateinit var snackView :View
	private lateinit var itemsDescription :List<ProductDetails>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.supportActionBar?.hide()
		setContentView(R.layout.activity_about)

		textProjectUrl    .movementMethod = LinkMovementMethod.getInstance()
		textPlayMarket    .movementMethod = LinkMovementMethod.getInstance()
		textFDroid        .movementMethod = LinkMovementMethod.getInstance()
		aboutFdroidLetters.movementMethod = LinkMovementMethod.getInstance()
		aboutFdroidMemory .movementMethod = LinkMovementMethod.getInstance()

		@Suppress("DEPRECATION") val info :PackageInfo? = packageManager.getPackageInfo(packageName, 0)
		@Suppress("DEPRECATION") val version = "${info?.versionName} (${info?.versionCode})"
		textVersion.text = version
		buttonBuy.setOnClickListener {
			launchBillingFlow(spinnerItems.selectedItemPosition)
		}
		client = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build()
		client.startConnection(this)
	}

	override fun onResume() {
		super.onResume()
		snackView = textFDroid
	}

	override fun onBillingSetupFinished(billingResult: BillingResult) {
		Log.d("BILLING", "Listener Thread id: ${Thread.currentThread().id}")
		if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
			lifecycleScope.launch {
				val result = getProducts()
				itemsDescription = result.productDetailsList!!.sortedWith(compareBy { it.productId })
				billingReady()
			}
			Thread.currentThread().run {  }
		}
	}

	private fun billingReady() {
		spinnerItems.adapter = AboutProductDetailsAdapter(this, itemsDescription)
		buttonBuy.isEnabled = true
		buttonBuy.setText(R.string.about_order)
	}

	override fun onBillingServiceDisconnected() {
		Snackbar.make(snackView, R.string.about_services_disconnected, Snackbar.LENGTH_LONG).show()
	}

	override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
		when(billingResult.responseCode) {
			BillingClient.BillingResponseCode.OK -> {
				purchases?.let {
					for (purchase in it) {
						if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
							val param = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
							client.consumeAsync(param) { billingResultAsync, _ ->
								if (billingResultAsync.responseCode == BillingClient.BillingResponseCode.OK) {
									Snackbar.make(snackView, R.string.about_order_successful, Snackbar.LENGTH_LONG).show()
									Log.d("BILLING", "CONSUMED SUCCESSFULLY")
								}
								Log.d("BILLING", "Unsuccessful result code")
							}
						}
					}
				}
			}
			BillingClient.BillingResponseCode.USER_CANCELED -> {
				Snackbar.make(snackView, R.string.about_order_cancelled, Snackbar.LENGTH_LONG).show()
				Log.d("BILLING", "order cancelled")
			}
			BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
				Snackbar.make(snackView, R.string.about_order_pending, Snackbar.LENGTH_LONG).show()
				Log.d("BILLING", "Item order is in pending state")
			}
			BillingClient.BillingResponseCode.ERROR -> {
				Snackbar.make(snackView, R.string.about_order_failed, Snackbar.LENGTH_LONG).show()
				Log.d("BILLING", "order unsuccessful")
			}
			else -> { Log.d("BILLING", "SOME ERROR OCCURRED: ${billingResult.responseCode}") }
		}
	}

	private fun launchBillingFlow(selectedItemPosition :Int) {
		val detail = (spinnerItems.adapter as AboutProductDetailsAdapter).getProductDetails(selectedItemPosition)
		detail?.let {
			val productDetailsParamsList = listOf(
				BillingFlowParams.ProductDetailsParams.newBuilder()
					.setProductDetails(it)
					.build()
			)
			val flowParams = BillingFlowParams.newBuilder()
				.setProductDetailsParamsList(productDetailsParamsList)
				.build()
			client.launchBillingFlow(this, flowParams)
		}
	}

	private suspend fun getProducts() :ProductDetailsResult {
		val skuList = arrayListOf("dn01", "dn02", "dn03", "dn05", "dn10", "dn15", "dn20")
		val productList = ArrayList<QueryProductDetailsParams.Product>()
		skuList.forEach {
			productList.add(QueryProductDetailsParams.Product.newBuilder()
			.setProductId(it)
			.setProductType(BillingClient.ProductType.INAPP)
			.build())
		}
		val params = QueryProductDetailsParams.newBuilder()
		params.setProductList(productList)
		Log.d("BILLING", "getProducts")
		return client.queryProductDetails(params.build())
	}
}
