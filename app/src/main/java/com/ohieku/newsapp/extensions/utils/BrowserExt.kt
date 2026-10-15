package com.ohieku.newsapp.extensions.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.toArgb
import com.ohieku.newsapp.R
import com.ohieku.newsapp.ui.compose.theme.ChromeDark
import com.ohieku.newsapp.ui.compose.theme.Ink

fun Context.openInBrowser(url: String) {
    val uri = Uri.parse(url)
    if (uri.scheme?.lowercase() !in setOf("http", "https")) return

    try {
        // Keep the broadcast chrome around the publisher's page instead of Chrome's default light toolbar
        val lightToolbar = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(Ink.toArgb())
            .setNavigationBarColor(Ink.toArgb())
            .build()
        val darkToolbar = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ChromeDark.toArgb())
            .setNavigationBarColor(ChromeDark.toArgb())
            .build()
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setDefaultColorSchemeParams(lightToolbar)
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, darkToolbar)
            .build()
            .launchUrl(this, uri)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.no_browser_found, Toast.LENGTH_SHORT).show()
    }
}
