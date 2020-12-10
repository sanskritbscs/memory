package com.sanskritbasics.memory

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.SpinnerAdapter


class BillingMediator {
	fun setParams(cont :Context, view :View) {}
	fun getAdapter() :SpinnerAdapter { return null as SpinnerAdapter }
	fun init( func :(Boolean) -> Any ) { func(false) }
	fun launchBillingFlow(activity :Activity, selectedItemPosition :Int) {}
}
