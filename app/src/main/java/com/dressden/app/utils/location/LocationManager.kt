package com.dressden.app.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dressden.app.utils.Constants
import com.dressden.app.utils.permissions.PermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager
) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    
    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        Constants.LOCATION_UPDATE_INTERVAL
    ).apply {
        setMinUpdateIntervalMillis(Constants.FASTEST_LOCATION_INTERVAL)
        setMaxUpdateDelayMillis(Constants.LOCATION_UPDATE_INTERVAL * 2)
    }.build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _currentLocation.postValue(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onError: (Exception) -> Unit) {
        if (!permissionManager.checkLocationPermission()) {
            onError(SecurityException("Location permission not granted"))
            return
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(
        onSuccess: (Location) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (!permissionManager.checkLocationPermission()) {
            onError(SecurityException("Location permission not granted"))
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let(onSuccess) ?: onError(Exception("Location not available"))
            }
            .addOnFailureListener(onError)
    }

    fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude,
            results
        )
        return results[0]
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    companion object {
        private const val TAG = "LocationManager"
    }
}
