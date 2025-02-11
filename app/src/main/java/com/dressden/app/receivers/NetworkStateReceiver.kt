package com.dressden.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class NetworkStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            // Network is connected
            Toast.makeText(context, "Network Connected", Toast.LENGTH_SHORT).show()
        } else {
            // Network is disconnected
            Toast.makeText(context, "Network Disconnected", Toast.LENGTH_SHORT).show()
        }
    }
}
