package com.jumbox.app.muslim.di

import com.jumbox.app.muslim.receiver.ReminderReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Jumadi Janjaya date on 28/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Module
abstract class BroadcastReceiverModule {

    @ContributesAndroidInjector
    abstract fun contributesReminderReceiver() : ReminderReceiver

}