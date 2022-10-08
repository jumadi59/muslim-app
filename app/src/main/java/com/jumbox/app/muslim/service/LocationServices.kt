package com.jumbox.app.muslim.service

import android.content.Context
import android.location.*
import android.os.Bundle
import java.io.IOException
import java.util.*

/**
 * Created by Jumadi Janjaya date on 09/12/2020.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class LocationServices(private val context: Context) {

    companion object {

        @Throws(IOException::class)
        fun getLocationAddress(context: Context, lat: Double, lang: Double): Address? {
            val decoder = Geocoder(context, Locale.getDefault())
            val data = decoder.getFromLocation(lat, lang, 1)
            return if (data != null) {
                when (data.isNotEmpty()) {
                    true -> data[0]
                    false -> null
                }
            } else null
        }
    }

    private var manager: LocationManager? = null
    var location: Location? = null
    private var countRetry = 0

    @Throws(SecurityException::class)
    fun services() {
        manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        when {
            manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                manager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L, 100F, locationListenerGPS)
            }
            manager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                manager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L, 100F, locationListenerGPS)
            }
            else -> {
                manager!!.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                        2000L, 100F, locationListenerGPS)
            }
        }

    }

    private var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(loc: Location) {
            location = loc
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @Throws(SecurityException::class)
    fun getLastLocation() {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        location = lm?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location == null)
            location = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }


    fun stopUsingGPS() {
        manager?.removeUpdates(locationListenerGPS)
    }

    fun retry() : Boolean {
        countRetry++
        return if (countRetry > 3) {
            getLastLocation()
            false
        } else {
            services()
            true
        }
    }
}