package pl.lipov.eventslogger.common.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlin.math.abs

class SensorEventsUtils : SensorEventListener {

    val onNoAccelerometerDetected = MutableLiveData<Unit>()
    val onEventReceived = MutableLiveData<String>()
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lastAccelerationX = 0f
    private var lastAccelerationY = 0f
    private var lastAccelerationZ = 0f

    override fun onSensorChanged(
        sensorEvent: SensorEvent
    ) {
        var accelerationChanged = false
        val acceleration = sensorEvent.values
        val accelerationX = acceleration[0]
        if (abs(accelerationX - lastAccelerationX) >= 1) {
            accelerationChanged = true
        }
        lastAccelerationX = accelerationX
        val accelerationY = acceleration[1]
        if (abs(accelerationY - lastAccelerationY) >= 1) {
            accelerationChanged = true
        }
        lastAccelerationY = accelerationY
        val accelerationZ = acceleration[2]
        if (abs(accelerationZ - lastAccelerationZ) >= 1) {
            accelerationChanged = true
        }
        lastAccelerationZ = accelerationZ
        if (accelerationChanged) {
            onEventReceived.postValue(
                "Acceleration changed [" +
                        "${accelerationX.getFormattedAcceleration("X")}; " +
                        "${accelerationY.getFormattedAcceleration("Y")}; " +
                        "${accelerationZ.getFormattedAcceleration("Z")}]"
            )
        }
    }

    private fun Float.getFormattedAcceleration(
        coordinate: String
    ): String = String.format("$coordinate: %.1f", this)

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) {
        Log.d("sensor_events_utils", "${sensor.name} accuracy changed to $accuracy.")
    }

    fun initAccelerometer(
        context: Context
    ) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            onNoAccelerometerDetected.postValue(Unit)
        }
    }

    fun registerSensorEventListener() {
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregisterSensorEventListener() {
        sensorManager?.unregisterListener(this)
    }
}
