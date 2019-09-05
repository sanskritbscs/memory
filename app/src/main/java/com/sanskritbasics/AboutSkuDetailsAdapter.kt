package com.sanskritbasics

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.billingclient.api.*

class AboutSkuDetailsAdapter(private val context :Context, private val items :List<SkuDetails>) :BaseAdapter() {
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