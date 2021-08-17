package com.jumbox.app.muslim.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.jumbox.app.muslim.data.pref.Preference
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Jumadi Janjaya date on 27/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

abstract class BasePreferenceActivity<VH : ViewDataBinding> : DaggerAppCompatActivity()  {

    lateinit var binding: VH

    @Inject
    lateinit var preference: Preference

    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun initView()
    abstract fun initData(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())

        initView()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        initData(savedInstanceState)
    }
}