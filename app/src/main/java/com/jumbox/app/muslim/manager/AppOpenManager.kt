package com.jumbox.app.muslim.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.jumbox.app.muslim.BuildConfig
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.data.pref.Preference
import java.util.*

/**
 * Created by Jumadi Janjaya date on 21/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class AppOpenManager(private val application: Application) : LifecycleObserver, Application.ActivityLifecycleCallbacks,
    AppOpenAd.AppOpenAdLoadCallback() {

    companion object {
        val TAG: String = AppOpenManager::class.java.simpleName
        const val AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294"
        var isShowingAd = false
        var callbackDismiss: (() -> Unit)? = null

    }

    private var currentActivity: Activity? = null
    private val adRequest = AdRequest.Builder().build()
    private var appOpenAd: AppOpenAd? = null
    private var loadTime = 0L
    private val isAdAvailable: Boolean
    get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    private val preference = Preference(application)

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
        Log.d(TAG, "onStart")
    }

    fun fetchAd() {
        if (isAdAvailable) return
        AppOpenAd.load(application, if (BuildConfig.DEBUG) AD_UNIT_ID else application.getString(R.string.ad_unit_id_open_app), adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, this)
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable) {
            Log.d(TAG, "Will show ad.")
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    callbackDismiss?.invoke()
                    isShowingAd = false
                    fetchAd()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    isShowingAd = false
                    fetchAd()
                }
            }
            if (preference.isInitialize && currentActivity != null) appOpenAd?.show(currentActivity!!)

        } else {
            fetchAd()
            Log.d(TAG, "Can not show ad.")
        }
    }

    private fun wasLoadTimeLessThanNHoursAgo() : Boolean {
        val dateDifference = Date().time - loadTime
        val numMillSecondsPerHours = 3600000
        return (dateDifference < (numMillSecondsPerHours * 4))
    }

    override fun onAdLoaded(p0: AppOpenAd) {
        appOpenAd = p0
        loadTime = Date().time
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(p0: Activity) {}
    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {
        currentActivity = null
    }

}