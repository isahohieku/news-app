package com.ohieku.newsapp.extensions.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.ohieku.newsapp.R

fun Context.openInBrowser(url: String) {
    val uri = Uri.parse(url)
    if (uri.scheme?.lowercase() !in setOf("http", "https")) return

    try {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(this, uri)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.no_browser_found, Toast.LENGTH_SHORT).show()
    }
}
