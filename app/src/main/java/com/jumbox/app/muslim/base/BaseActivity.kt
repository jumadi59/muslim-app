package com.jumbox.app.muslim.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.jumbox.app.muslim.di.ViewModelFactory
import com.jumbox.app.muslim.receiver.ReminderReceiver.Companion.ACTION_REMINDER
import com.jumbox.app.muslim.ui.prayer.AdzanDialogFragment
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Created by Jumadi Janjaya date on 19/07/2020.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

abstract class BaseActivity<VH : ViewDataBinding, VM: ViewModel> : DaggerAppCompatActivity() {

    companion object{
        const val EXTRA_PRAYER_NOW = "extra_prayer_now"
    }

    lateinit var binding: VH

    @Inject
    lateinit var factory: ViewModelFactory
    protected val viewModel: VM by lazy { ViewModelProvider(this, factory).get(getViewModelClass().java) }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == ACTION_REMINDER) {
                intent.getLongExtra(EXTRA_PRAYER_NOW, 0L).let {
                    if (it > 0L) {
                        with(AdzanDialogFragment(it)) {
                            isCancelable = false
                            show(supportFragmentManager, "adzan_dialog")
                        }
                    }
                }
            }
        }
    }
    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun getViewModelClass(): KClass<VM>

    protected open fun initView() {}
    abstract fun initData(savedInstanceState: Bundle?)
    protected open fun initView(savedInstanceState: Bundle?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())

        initView()
        initView(savedInstanceState)
        chekOrientation(resources.configuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        chekOrientation(newConfig)
    }

    private fun chekOrientation(configuration: Configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Daiya", "ORIENTATION_LANDSCAPE")
            if (Build.VERSION.SDK_INT >= 30) {
                binding.root.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                binding.root.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LOW_PROFILE or
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("Daiya", "ORIENTATION_PORTRAIT")
            if (Build.VERSION.SDK_INT >= 30) {
                binding.root.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                binding.root.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        handleIntent(intent)
        initData(savedInstanceState)
    }

    fun createToast(text: String) : Toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)

    fun createSnackBar(text: String) : Snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT)

    open fun handleIntent(intent: Intent?) {}


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter(ACTION_REMINDER))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    protected fun <VM : ViewModel> viewModel(viewModel : KClass<VM>): Lazy<VM> {
        return lazy { ViewModelProvider(this, factory).get(viewModel.java) }
    }

}