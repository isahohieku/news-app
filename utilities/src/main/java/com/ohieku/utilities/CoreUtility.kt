package com.ohieku.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class CoreUtility {
    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        val result = when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false

        }
        return result
    }
}