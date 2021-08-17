package com.jumbox.app.muslim.di

import com.jumbox.app.muslim.ui.quran.BookmarkFragment
import com.jumbox.app.muslim.ui.quran.SurahFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Jumadi date on 26/07/20
 * Bengkulu, Indonesia
 */

@Suppress("unused")
@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeSurahFragment() : SurahFragment

    @ContributesAndroidInjector
    abstract fun contributeBookmarkFragment() : BookmarkFragment

}
