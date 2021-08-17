package com.jumbox.app.muslim.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.jumbox.app.muslim.di.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Created by Jumadi Janjaya date on 19/07/2020.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

abstract class BaseFragment<VH : ViewDataBinding, VM: ViewModel> : DaggerFragment(){

    lateinit var binding: VH

    @Inject
    lateinit var factory: ViewModelFactory
    protected val viewModel: VM by lazy { ViewModelProvider(this, factory).get(getViewModelClass().java) }

    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun getViewModelClass(): KClass<VM>

    abstract fun initView()
    abstract fun initData(savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        handleArguments(arguments)
        initData(savedInstanceState)
    }

    open fun handleArguments(bundle: Bundle?) {
    }

    fun createToast(text: String) : Toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
    fun createSnackBar(text: String) : Snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT)

    protected fun <VM : ViewModel> viewModel(viewModel : KClass<VM>): Lazy<VM> {
        return lazy { ViewModelProvider(this, factory).get(viewModel.java) }
    }
}