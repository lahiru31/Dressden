package com.dressden.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStateReceiver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetworkState()
        }

        override fun onLost(network: Network) {
            updateNetworkState()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            updateNetworkState()
        }
    }

    init {
        registerNetworkCallback()
        updateNetworkState()
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun updateNetworkState() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        val state = when {
            networkCapabilities == null -> NetworkState.Unavailable
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                val hasInternet = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                val validated = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
                if (hasInternet && validated) {
                    NetworkState.Available(
                        type = NetworkType.WIFI,
                        strength = getWifiStrength()
                    )
                } else {
                    NetworkState.Limited(NetworkType.WIFI)
                }
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                val hasInternet = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                val validated = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
                if (hasInternet && validated) {
                    NetworkState.Available(
                        type = NetworkType.CELLULAR,
                        strength = getCellularStrength()
                    )
                } else {
                    NetworkState.Limited(NetworkType.CELLULAR)
                }
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ->
                NetworkState.Available(NetworkType.VPN)
            else -> NetworkState.Unavailable
        }

        _networkState.postValue(state)
    }

    private fun getWifiStrength(): Int {
        return try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
            val rssi = wifiManager.connectionInfo.rssi
            WifiManager.calculateSignalLevel(rssi, 5)
        } catch (e: Exception) {
            -1
        }
    }

    private fun getCellularStrength(): Int {
        return try {
            val telephonyManager = 
                context.getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signalStrength = telephonyManager.signalStrength
                signalStrength?.level ?: -1
            } else {
                -1
            }
        } catch (e: Exception) {
            -1
        }
    }

    sealed class NetworkState {
        data class Available(
            val type: NetworkType,
            val strength: Int = -1
        ) : NetworkState()

        data class Limited(val type: NetworkType) : NetworkState()
        object Unavailable : NetworkState()
    }

    enum class NetworkType {
        WIFI,
        CELLULAR,
        VPN
    }

    companion object {
        private const val TAG = "NetworkStateReceiver"
    }
}
