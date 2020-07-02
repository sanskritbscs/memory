package com.sanskritbasics.memory

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.billingclient.api.*
import com.google.android.material.snackbar.Snackbar

class BillingMediator :PurchasesUpdatedListener {
	private lateinit var context :Context
	private lateinit var client  :BillingClient
	private lateinit var snackView :View
	private lateinit var itemsDescription :MutableList<SkuDetails>
	private lateinit var adapter :AboutSkuDetailsAdapter

	val isAvailable = true

	class AboutSkuDetailsAdapter(private val context :Context, private val items :List<SkuDetails>) : BaseAdapter() {
		private val layoutInflater = LayoutInflater.from(context)

		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view  = convertView ?: layoutInflater.inflate(R.layout.support_simple_spinner_dropdown_item,null)
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
		override fun getItem  (position: Int) :SkuDetails { return items[position]    }

		fun getSkuDetails(position: Int) :SkuDetails? { return if (items.size > 0) items[position] else null }
	}



	fun setParams(cont :Context, view :View) {
		context = cont
		snackView = view
	}

	fun getAdapter() :AboutSkuDetailsAdapter {
		return adapter
	}

	fun init( func :(Boolean) -> Any ) {
		client = BillingClient.newBuilder(context).setListener(this).enablePendingPurchases().build()
		client.startConnection(object : BillingClientStateListener {
			override fun onBillingSetupFinished(billingResult: BillingResult) {
				if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
					val skuList = arrayListOf("dn01", "dn02", "dn03", "dn05", "dn10", "dn15", "dn20")
					val params = SkuDetailsParams.newBuilder()
					params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
					client.querySkuDetailsAsync(params.build()) { result, skuDetailsList ->
//						Log.d("!!!!!!!!!!!!!!!", "Result is ${result.responseCode}")
						if (result.responseCode == BillingClient.BillingResponseCode.OK) {
//							Log.d("!!!!!!!!!!!!!!!", "Number in List: ${skuDetailsList.size}")
							skuDetailsList?.let { sdl ->
								sdl.sortWith(compareBy { it.sku })
								itemsDescription = sdl
								adapter = AboutSkuDetailsAdapter(context, itemsDescription)
								func(true)
							}
						}
					}
				} else { func(false) }
			}
			override fun onBillingServiceDisconnected() {
				Snackbar.make(snackView, R.string.about_services_disconnected, Snackbar.LENGTH_LONG).show()
			}
		})
	}

	override fun onPurchasesUpdated(billingResult :BillingResult, purchases :MutableList<Purchase>?) {
		when(billingResult.responseCode) {
			BillingClient.BillingResponseCode.OK -> {
				purchases?.let {
					for (purchase in purchases) {
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
			else -> { Log.d("BILLING", "SOME ERROR OCCURRED: ${billingResult?.responseCode}") }
		}
	}

	fun launchBillingFlow(activity :Activity, selectedItemPosition :Int) {
		val detail = adapter.getSkuDetails(selectedItemPosition)
		detail?.let {
			val flowParams = BillingFlowParams.newBuilder()
				.setSkuDetails(it)
				.build()
			client.launchBillingFlow(activity, flowParams)
		}
	}
}
