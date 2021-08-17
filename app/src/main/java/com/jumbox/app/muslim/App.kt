package com.jumbox.app.muslim

import com.google.android.gms.ads.MobileAds
import com.jumbox.app.muslim.di.DaggerAppComponent
import com.jumbox.app.muslim.manager.AppOpenManager
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build().apply { inject(this@App) }
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        AppOpenManager(this)
    }
}