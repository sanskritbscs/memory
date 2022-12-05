package com.sanskritbasics.memory

import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity :AppCompatActivity() {

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
		billing.visibility = View.GONE
	}
}
