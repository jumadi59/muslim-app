package com.jumbox.app.muslim.di

import com.jumbox.app.muslim.ui.main.BottomSheetFindRegion
import com.jumbox.app.muslim.ui.prayer.AdzanDialogFragment
import com.jumbox.app.muslim.ui.quran.BottomSheetListSurah
import com.jumbox.app.muslim.ui.quran.BottomSheetTafsir
import com.jumbox.app.muslim.ui.zikir.FragmentBottomSheetInput
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
@Suppress("unused")
@Module
abstract class DialogFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeBottomSheetFindRegion() : BottomSheetFindRegion

    @ContributesAndroidInjector
    abstract fun contributeBottomSheetListSurah() : BottomSheetListSurah

    @ContributesAndroidInjector
    abstract fun contributeAdzanDialogFragment() : AdzanDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeBottomSheetTafsir() : BottomSheetTafsir

    @ContributesAndroidInjector
    abstract fun contributeFragmentBottomSheetInput() : FragmentBottomSheetInput
}