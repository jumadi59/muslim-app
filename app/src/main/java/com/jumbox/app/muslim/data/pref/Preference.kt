package com.jumbox.app.muslim.data.pref

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Singleton
class Preference constructor(context: Context) {

    @Inject constructor(app: Application) : this(app.baseContext)

    private var prefs = PreferenceManager.getDefaultSharedPreferences(context)

    private fun savePref(): SharedPreferences.Editor = prefs.edit()

    var isInitialize: Boolean
        get() = prefs.getBoolean("initialize", false)
        set(value) {
            val editor = savePref()
            editor.putBoolean("initialize", value)
            editor.apply()
        }

    var locationId: String?
        get() = prefs.getString("location_id", "")
        set(value) {
            val editor = savePref()
            editor.putString("location_id", value)
            editor.apply()
        }
    var city: String?
        get() = prefs.getString("city", "")
        set(value) {
            val editor = savePref()
            editor.putString("city", value)
            editor.apply()
        }

    var rintone: String?
        get() = prefs.getString("ringtone", "")
        set(value) {
            val editor = savePref()
            editor.putString("ringtone", value)
            editor.apply()
        }

    var alarmTimeOut: Int
        get() = prefs.getInt("alarm_time_out", 0)
        set(value) {
            val editor = savePref()
            editor.putInt("alarm_time_out", value)
            editor.apply()
        }

    var notifications: List<String>
        get() {
            val value = prefs.getString("notify_enabled", "")
            return value!!.split(",")
        }
        set(value) {
            val editor = savePref()
            editor.putString("notify_enabled", value.joinToString(","))
            editor.apply()
        }

    var alarmCorrectionTime: List<String>
    get() {
        val value = prefs.getString("alarm_correction_time", "0,0,0,0,0")
        return  value!!.split(",")
    }
    set(value) {
        val editor = savePref()
        editor.putString("alarm_correction_time", value.joinToString(","))
        editor.apply()
    }

    var tasbihMaxCounter: Int
        get() = prefs.getInt("tasbih_max_counter", 33)
        set(value) {
            val editor = savePref()
            editor.putInt("tasbih_max_counter", value)
            editor.apply()
        }

    var tasbihVibrate: Boolean
        get() = prefs.getBoolean("tasbih_vibrate", false)
        set(value) {
            val editor = savePref()
            editor.putBoolean("tasbih_vibrate", value)
            editor.apply()
        }

    var tasbihReverse: Boolean
        get() = prefs.getBoolean("tasbih_reverse", false)
        set(value) {
            val editor = savePref()
            editor.putBoolean("tasbih_reverse", value)
            editor.apply()
        }
}