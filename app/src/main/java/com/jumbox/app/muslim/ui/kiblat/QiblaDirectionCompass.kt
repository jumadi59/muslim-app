package com.jumbox.app.muslim.ui.kiblat

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.widget.Toast


/**
 * Created by Jumadi Janjaya date on 20/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class QiblaDirectionCompass(
    private val context: Context,
    private val compassListener: (Float, Float, Float, Float) -> Unit
) : SensorEventListener {

    private var sensorManager: SensorManager = (context.getSystemService(SENSOR_SERVICE) as SensorManager)
    private var accelerometerSensor: Sensor? = null
    private var magnetSensor: Sensor? = null
    private val aData = FloatArray(3)
    private val mData = FloatArray(3)
    private val r = FloatArray(9)
    private val i = FloatArray(9)
    var userLoc: Location? = null
    private var currentDegree = 0f
    private var currentDegreeNeedle = 0f

    init {
        try {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun onCreate() {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_GAME)
        val manager = context.packageManager
        val haveAS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)
        val haveCS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)

        if (!haveAS || !haveCS) {
            sensorManager.unregisterListener(this, accelerometerSensor)
            sensorManager.unregisterListener(this, magnetSensor)
            Toast.makeText(context, "Not Supported", Toast.LENGTH_SHORT).show()
        }
    }

    fun onDestroy() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {

        val alpha = 0.97f

        synchronized(this) {
            if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                aData[0] = alpha * aData[0] + (1 - alpha) * sensorEvent.values[0]
                aData[1] = alpha * aData[1] + (1 - alpha) * sensorEvent.values[1]
                aData[2] = alpha * aData[2] + (1 - alpha) * sensorEvent.values[2]
            }
            if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mData[0] = alpha * mData[0] + (1 - alpha)* sensorEvent.values[0]
                mData[1] = alpha * mData[1] + (1 - alpha)* sensorEvent.values[1]
                mData[2] = alpha * mData[2] + (1 - alpha)* sensorEvent.values[2]
            }

            val success = SensorManager.getRotationMatrix(r, i, aData, mData)
            if (userLoc == null || !success) {
                return
            }
            val destinationLoc = Location("service Provider")
            destinationLoc.latitude = 21.422487
            destinationLoc.longitude = 39.826206
            var bearTo: Float = userLoc!!.bearingTo(destinationLoc).toDouble().toFloat()

            val orientation = FloatArray(3)
            SensorManager.getOrientation(r, orientation)
            val degree = Math.toDegrees(orientation[0].toDouble()).toFloat()
            var head = Math.toDegrees(orientation[0].toDouble()).toFloat()

            val geoField = GeomagneticField(
                userLoc!!.latitude.toFloat(),
                userLoc!!.longitude.toFloat(),
                userLoc!!.altitude.toFloat(),
                System.currentTimeMillis()
            )
            head -= geoField.declination

            if (bearTo < 0) {
                bearTo += 360
            }
            var direction = bearTo - head
            if (direction < 0) {
                direction += 360
            }
            compassListener.invoke(degree, currentDegreeNeedle, currentDegree, direction)
            currentDegreeNeedle = direction
            currentDegree = -degree
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }
}