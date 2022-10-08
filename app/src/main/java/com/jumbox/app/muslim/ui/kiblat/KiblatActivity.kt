package com.jumbox.app.muslim.ui.kiblat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.databinding.ActivityKiblatBinding
import com.jumbox.app.muslim.service.LocationServices
import com.jumbox.app.muslim.ui.main.MainViewModel
import com.jumbox.app.muslim.utils.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class KiblatActivity : BaseActivity<ActivityKiblatBinding, MainViewModel>() {


    private lateinit var qiblaDirectionCompass: QiblaDirectionCompass
    private val gps: LocationServices by lazy { LocationServices(this) }

    override fun getLayoutId() = R.layout.activity_kiblat

    override fun getViewModelClass() = MainViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
        }

        qiblaDirectionCompass = QiblaDirectionCompass(this) {
            degree: Float,
            currentDegreeNeedle: Float,
            currentDegree: Float,
            direction: Float ->
            RotateAnimation(currentDegreeNeedle, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
                duration = 210
                fillAfter = true
                binding.caabaCompass.startAnimation(this)
            }
            RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
                duration = 210
                fillAfter = true
                binding.indicator.startAnimation(this)
            }
        }

        PermissionManager.cekPermissionLocation(this, {
            gps.services()
            compass()
        }) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 9000)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        qiblaDirectionCompass.onCreate()
    }

    override fun onPause() {
        qiblaDirectionCompass.onDestroy()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        qiblaDirectionCompass.onDestroy()
        gps.stopUsingGPS()
    }

    private fun compass() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(100)
            if (gps.location == null) {
                if (gps.retry()) {
                    delay(200)
                    compass()
                }
            }
            qiblaDirectionCompass.userLoc = gps.location
            qiblaDirectionCompass.onCreate()
            try {
                @Suppress("BlockingMethodInNonBlockingContext")
                val address = LocationServices.getLocationAddress(this@KiblatActivity,
                    gps.location!!.latitude,
                    gps.location!!.longitude)

                GlobalScope.launch(Dispatchers.Main) {
                    binding.province.text = address?.adminArea ?: getString(R.string.title_error)
                    binding.city.text = address?.subAdminArea ?: getString(R.string.title_error)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d("KiblatActivity", "${gps.location}")
        }
    }

}