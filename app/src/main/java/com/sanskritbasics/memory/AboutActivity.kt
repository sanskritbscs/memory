package com.sanskritbasics.memory

import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity :AppCompatActivity() {

	private val billingClient = BillingMediator()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.supportActionBar?.hide()
		setContentView(R.layout.activity_about)

		textProjectUrl    .movementMethod = LinkMovementMethod.getInstance()
		textPlayMarket    .movementMethod = LinkMovementMethod.getInstance()
		textFDroid        .movementMethod = LinkMovementMethod.getInstance()
		aboutFdroidLetters.movementMethod = LinkMovementMethod.getInstance()
		aboutFdroidMemory .movementMethod = LinkMovementMethod.getInstance()

		val info :PackageInfo? = packageManager.getPackageInfo(packageName, 0)
		@Suppress("DEPRECATION") val version = "${info?.versionName} (${info?.versionCode})"
		textVersion.text = version

		billingClient.setParams(applicationContext, textFDroid)
		billingClient.init {
			if (it) {
				spinnerItems.adapter = billingClient.getAdapter()
			} else {
				buttonBuy.isEnabled = false
				spinnerItems.isEnabled = false
				billing.visibility = View.GONE
				Snackbar.make(textFDroid, R.string.about_services_unavailable, Snackbar.LENGTH_LONG).show()
			}
		}

		buttonBuy.setOnClickListener {
			billingClient.launchBillingFlow(this, spinnerItems.selectedItemPosition)
		}
	}
}
