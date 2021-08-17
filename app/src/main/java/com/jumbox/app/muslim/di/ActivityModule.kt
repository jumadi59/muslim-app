package com.jumbox.app.muslim.di

import com.jumbox.app.muslim.ui.spalsh.StartInitializeActivity
import com.jumbox.app.muslim.ui.asmaulhusna.AsmaulHusnaActivity
import com.jumbox.app.muslim.ui.calendar.CalendarActivity
import com.jumbox.app.muslim.ui.hadist.HadistActivity
import com.jumbox.app.muslim.ui.hadist.HadistListActivity
import com.jumbox.app.muslim.ui.kiblat.KiblatActivity
import com.jumbox.app.muslim.ui.main.MainActivity
import com.jumbox.app.muslim.ui.prayer.PrayerTimeActivity
import com.jumbox.app.muslim.ui.prayer.RingAdzanActivity
import com.jumbox.app.muslim.ui.quran.QuranActivity
import com.jumbox.app.muslim.ui.quran.QuranListActivity
import com.jumbox.app.muslim.ui.setting.AlaramBeforePreference
import com.jumbox.app.muslim.ui.setting.SettingsActivity
import com.jumbox.app.muslim.ui.setting.TimingCorrectionPreference
import com.jumbox.app.muslim.ui.spalsh.SplashActivity
import com.jumbox.app.muslim.ui.zikir.ZikirActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Suppress("unused")
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeMainActivity() : MainActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributePrayerTimeActivity() : PrayerTimeActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeQuranListActivity() : QuranListActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeQuranActivity() : QuranActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeAsmaulHusnaActivity() : AsmaulHusnaActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeKiblatActivity() : KiblatActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeDoaActivity() : HadistActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeHadistListActivity() : HadistListActivity

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity() : SplashActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeStartInitializeActivity() : StartInitializeActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeRingAdzanActivity() : RingAdzanActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeCalendarActivity() : CalendarActivity

    @ContributesAndroidInjector(modules = [DialogFragmentModule::class])
    abstract fun contributeZikirActivity() : ZikirActivity

    @ContributesAndroidInjector
    abstract fun contributeSettingsActivity() : SettingsActivity

    @ContributesAndroidInjector
    abstract fun contributeTimingCorrectionPreference() : TimingCorrectionPreference

    @ContributesAndroidInjector
    abstract fun contributeAlarmBeforePreference() : AlaramBeforePreference
}