package com.datasoft.downloadManager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.webkit.URLUtil
import java.net.HttpURLConnection
import java.net.URL

object UrlUtils {

    fun isUrlValid(url: String) = URLUtil.isValidUrl(url)

    fun isUrlReachable(urlStr: String): Boolean {
        if (!isUrlValid(urlStr)) return false

        val url = URL(urlStr)
        val connection: HttpURLConnection =
                url.openConnection() as HttpURLConnection
        val code: Int = connection.responseCode
        return code in 200..209
    }


    fun isOnline(context: Context): Boolean {
        val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
            return false
        } else
            return (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isAvailable
                    && connectivityManager.activeNetworkInfo!!.isConnected)
    }


}

fun Context.isOnline() = UrlUtils.isOnline(this)