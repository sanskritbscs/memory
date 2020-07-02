package com.sanskritbasics

import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {

	private lateinit var lWebView :WebView

	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.supportActionBar!!.hide()
		setContentView(R.layout.activity_main)

		lWebView = findViewById(R.id.webView1)
		lWebView.settings.javaScriptEnabled = true
		lWebView.settings.domStorageEnabled = true
		lWebView.settings.setNeedInitialFocus(false)
		lWebView.setBackgroundColor(Color.TRANSPARENT)
		lWebView.webViewClient = if (android.os.Build.VERSION.SDK_INT < 24)
			object :WebViewClient() {
				override fun shouldOverrideUrlLoading(view: WebView, url: String?) :Boolean
				{ return processUrl(view, url) }
			}
		else
			object : WebViewClient() {
				override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean
				{ return processUrl(view, request.url.toString())	}
			}
		lWebView.webChromeClient = object :WebChromeClient() {
			override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
				Log.d("SanskritBasics", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId())
				return true
			}
		}
		// Unpack archive
		val buffer = ByteArray(2048)
		val cache = cacheDir
		val index = File(cache, "index.html")
		val pm = packageManager
		var updateTime = java.lang.Long.MAX_VALUE
		try {
			val pi = pm.getPackageInfo("com.sanskritbasics", 0)
			updateTime = pi.lastUpdateTime
		} catch (ignored: Exception) {}

		if (!index.exists() || updateTime > index.lastModified()) {
			Log.d("RES", "Updating resources")
			try {
				val zis = ZipInputStream(resources.openRawResource(R.raw.sanskritbasics))
				try {
					var ze: ZipEntry
					while (true) {
						ze = zis.nextEntry
						if (ze == null) break
						val baos = ByteArrayOutputStream()
						var count: Int
						READ@ while (true) {
							count = zis.read(buffer)
							if (count == -1) break@READ
							baos.write(buffer, 0, count)
						}
						val filename = File(cache, ze.name)
						val canonicalPath = filename.canonicalPath
						if (!canonicalPath.startsWith(cache.canonicalPath)) {
							Log.d("RES", "Wrong file path")
						}
						val bytes = baos.toByteArray()
						if (ze.isDirectory) filename.mkdirs()
						else {
							val out = BufferedOutputStream(FileOutputStream(filename))
							out.write(bytes)
							out.flush()
							out.close()
						}
					}
				} catch (ignored: Exception) {}
					finally {	zis.close()	}
			} catch (e: Exception) { e.printStackTrace() }

		} else Log.d("RES", "NOT Updating resources")
		lWebView.loadUrl(index.toURI().toString())
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
		if (keyCode == KeyEvent.KEYCODE_BACK && lWebView.canGoBack()) {
			lWebView.goBack()
			return true
		}
		return super.onKeyDown(keyCode, event)
	}

	fun processUrl(view :WebView, url :String?) :Boolean {
		if (url == null) return false
		return if (url.startsWith("about:about")) {
				val intent = Intent(this, AboutActivity::class.java)
				startActivity(intent)
				true
		}	else {	view.loadUrl(url);	false	}
	}

	// fix crash on android 5.0
	// https://stackoverflow.com/questions/58028821/
	override fun applyOverrideConfiguration(overrideConfiguration :Configuration) {
		if (Build.VERSION.SDK_INT in 21..22) {
			return
		}
		super.applyOverrideConfiguration(overrideConfiguration)
	}
}
