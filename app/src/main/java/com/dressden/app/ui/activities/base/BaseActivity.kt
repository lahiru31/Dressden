package com.dressden.app.ui.activities.base

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dressden.app.utils.Constants
import com.dressden.app.utils.location.LocationManager
import com.dressden.app.utils.media.MediaManager
import com.dressden.app.utils.notifications.NotificationManager
import com.dressden.app.utils.permissions.PermissionManager
import com.dressden.app.utils.sensors.SensorManager
import com.dressden.app.utils.telephony.TelephonyManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var mediaManager: MediaManager

    @Inject
    lateinit var telephonyManager: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeManagers()
    }

    private fun initializeManagers() {
        sensorManager.initialize()
        checkAndRequestPermissions()
        initializeLocationUpdates()
    }

    protected fun checkAndRequestPermissions() {
        permissionManager.checkAndRequestPermissions(this)
    }

    private fun initializeLocationUpdates() {
        if (permissionManager.checkLocationPermission()) {
            locationManager.startLocationUpdates { exception ->
                handleError("Location update failed", exception)
            }

            locationManager.getLastKnownLocation(
                onSuccess = { location -> handleLocationUpdate(location) },
                onError = { exception ->
                    handleError("Failed to get location", exception)
                }
            )
        }
    }

    protected open fun handleLocationUpdate(location: Location) {
        // Override in child classes if needed
    }

    protected fun handleError(message: String, exception: Exception) {
        Toast.makeText(
            this,
            "$message: ${exception.message}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            Constants.PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    onAllPermissionsGranted()
                } else {
                    onSomePermissionsDenied()
                }
            }
        }
    }

    protected open fun onAllPermissionsGranted() {
        notificationManager.showSystemNotification(
            "Permissions Granted",
            "You can now use all features of the app"
        )
        initializeLocationUpdates()
    }

    protected open fun onSomePermissionsDenied() {
        Toast.makeText(
            this,
            "Some features may not work without required permissions",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.startListening { sensorName, values ->
            handleSensorUpdate(sensorName, values)
        }
    }

    protected open fun handleSensorUpdate(sensorName: String, values: FloatArray) {
        // Override in child classes if needed
    }

    override fun onPause() {
        super.onPause()
        sensorManager.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.stopLocationUpdates()
        mediaManager.cleanup()
    }
}
