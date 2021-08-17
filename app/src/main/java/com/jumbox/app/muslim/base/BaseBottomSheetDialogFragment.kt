package com.jumbox.app.muslim.base

import android.content.Context
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jumbox.app.muslim.di.ViewModelFactory
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Created by Jumadi Janjaya date on 08/11/2020.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
abstract class BaseBottomSheetDialogFragment<VH : ViewDataBinding> : BottomSheetDialogFragment() {

    lateinit var binding: VH
    @Inject
    lateinit var factory: ViewModelFactory

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initView()
    abstract fun initData(savedInstanceState: Bundle?)

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initData(savedInstanceState)
    }

    fun createToast(text: String) : Toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)

    protected fun <VM : ViewModel> viewModel(viewModel : KClass<VM>): Lazy<VM> {
        return lazy { ViewModelProvider(this, factory).get(viewModel.java) }
    }
}