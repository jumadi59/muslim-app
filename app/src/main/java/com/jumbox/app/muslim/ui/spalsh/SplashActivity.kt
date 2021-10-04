package com.jumbox.app.muslim.ui.spalsh

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.data.pref.Preference
import com.jumbox.app.muslim.databinding.ActivitySplashBinding
import com.jumbox.app.muslim.receiver.ReminderReceiver
import com.jumbox.app.muslim.ui.main.MainActivity
import com.jumbox.app.muslim.vo.Status.*
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var preference: Preference
    lateinit var binding: ActivitySplashBinding

    private val countDownTimer = object : CountDownTimer(5000, 1000L) {

        @SuppressLint("SimpleDateFormat")
        override fun onTick(elapsedTime: Long) {}

        override fun onFinish() {
            if (preference.isInitialize) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, StartInitializeActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        Log.d("SplashActivity", "isReminder ${ReminderReceiver.isReminder(this)}")
        if (!ReminderReceiver.isReminder(this)) ReminderReceiver.updateAlarm(this)
        countDownTimer.start()
    }
}