package com.sanskritbasics.memory

import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import com.sanskritbasics.memory.databinding.ActivityAboutBinding

class AboutActivity :AppCompatActivity() {

	private lateinit var bd: ActivityAboutBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bd = ActivityAboutBinding.inflate(layoutInflater)
		setContentView(bd.root)
		this.supportActionBar?.hide()

		bd.textProjectUrl    .movementMethod = LinkMovementMethod.getInstance()
		bd.textPlayMarket    .movementMethod = LinkMovementMethod.getInstance()
		bd.textFDroid        .movementMethod = LinkMovementMethod.getInstance()
		bd.aboutFdroidLetters.movementMethod = LinkMovementMethod.getInstance()
		bd.aboutFdroidMemory .movementMethod = LinkMovementMethod.getInstance()

		@Suppress("DEPRECATION") val info :PackageInfo? = packageManager.getPackageInfo(packageName, 0)
		@Suppress("DEPRECATION") val version = "${info?.versionName} (${info?.versionCode})"
		bd.textVersion.text = version
		bd.billing.visibility = View.GONE
	}
}
