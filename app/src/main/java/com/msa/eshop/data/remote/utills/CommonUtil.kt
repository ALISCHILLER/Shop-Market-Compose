package com.msa.eshop.data.remote.utills

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.getSystemService

fun Context.hasActiveNetwork(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>() ?: return false

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager.activeNetwork != null
    } else {
        @Suppress("DEPRECATION")
        connectivityManager.activeNetworkInfo?.isConnected == true
    }
}

/*
 * این تابع فقط وجود شبکه فعال را چک می‌کند، نه validated بودن اینترنت بین‌الملل را.
 * برای اینترنت داخلی/سازمانی نباید request را با NET_CAPABILITY_VALIDATED بلاک کنیم.
 */
fun Context.isNetworkAvailable(): Boolean {
    return hasActiveNetwork()
}

fun Context.networkSnapshot(): NetworkSnapshot {
    val connectivityManager = getSystemService<ConnectivityManager>()
        ?: return NetworkSnapshot(
            hasActiveNetwork = false,
            description = "connectivity_manager=null"
        )

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        val info = connectivityManager.activeNetworkInfo

        return NetworkSnapshot(
            hasActiveNetwork = info?.isConnected == true,
            hasInternetCapability = info?.isAvailable == true,
            isValidated = null,
            transports = listOfNotNull(info?.typeName),
            description = "type=${info?.typeName}, connected=${info?.isConnected}, available=${info?.isAvailable}"
        )
    }

    val activeNetwork = connectivityManager.activeNetwork
        ?: return NetworkSnapshot(
            hasActiveNetwork = false,
            description = "active_network=null"
        )

    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        ?: return NetworkSnapshot(
            hasActiveNetwork = true,
            description = "capabilities=null"
        )

    val transports = buildList {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) add("wifi")
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) add("cellular")
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) add("ethernet")
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) add("vpn")
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) add("bluetooth")
    }

    val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    val isCaptivePortal = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)

    return NetworkSnapshot(
        hasActiveNetwork = true,
        hasInternetCapability = hasInternet,
        isValidated = isValidated,
        isCaptivePortal = isCaptivePortal,
        transports = transports,
        description = "transports=${transports.joinToString().ifBlank { "unknown" }}, hasInternet=$hasInternet, validated=$isValidated, captivePortal=$isCaptivePortal"
    )
}

data class NetworkSnapshot(
    val hasActiveNetwork: Boolean,
    val hasInternetCapability: Boolean? = null,
    val isValidated: Boolean? = null,
    val isCaptivePortal: Boolean? = null,
    val transports: List<String> = emptyList(),
    val description: String
)