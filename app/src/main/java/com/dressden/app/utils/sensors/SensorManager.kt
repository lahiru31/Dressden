package com.dressden.app.utils.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorManager @Inject constructor(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var proximitySensor: Sensor? = null

    private var onSensorChangedCallback: ((String, FloatArray) -> Unit)? = null

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    fun startListening(callback: (String, FloatArray) -> Unit) {
        onSensorChangedCallback = callback
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        onSensorChangedCallback = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensorName = when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
            Sensor.TYPE_GYROSCOPE -> "Gyroscope"
            Sensor.TYPE_PROXIMITY -> "Proximity"
            else -> "Unknown"
        }
        onSensorChangedCallback?.invoke(sensorName, event.values)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    fun isAccelerometerAvailable(): Boolean = accelerometer != null
    fun isGyroscopeAvailable(): Boolean = gyroscope != null
    fun isProximitySensorAvailable(): Boolean = proximitySensor != null
}
