package com.jumbox.app.muslim.di

import androidx.lifecycle.ViewModel
import com.jumbox.app.muslim.ui.hadist.HadistViewModel
import com.jumbox.app.muslim.ui.main.MainViewModel
import com.jumbox.app.muslim.ui.quran.QuranViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(QuranViewModel::class)
    abstract fun binQuranViewModel(viewModel: QuranViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HadistViewModel::class)
    abstract fun binHadistViewModel(viewModel: HadistViewModel) : ViewModel

}