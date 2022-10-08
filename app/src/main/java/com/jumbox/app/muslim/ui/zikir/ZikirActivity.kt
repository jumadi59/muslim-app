package com.jumbox.app.muslim.ui.zikir

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdRequest
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.data.pref.Preference
import com.jumbox.app.muslim.databinding.ActivityZikirBinding
import com.jumbox.app.muslim.receiver.ReminderReceiver
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class ZikirActivity : DaggerAppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "channel_02"
        const val CHANNEL_NAME = "Notification Tasbih Counter"
        const val EXTRA_CURRENT_COUNTER = "current_counter"
        const val EXTRA_MAX_COUNTER = "max_counter"
    }

    @Inject
    lateinit var preference: Preference
    lateinit var binding: ActivityZikirBinding
    private val fragmentBottomSheetInput: FragmentBottomSheetInput by lazy { FragmentBottomSheetInput(maxCounter.toString()) {
        maxCounter = it.toInt()
        preference.tasbihMaxCounter = maxCounter
        binding.tvMaxCounter.text = if (maxCounter == 0) "/*" else "/$it"
    } }
    private var currentCounter: Int = 0
    private var maxCounter: Int = 33
    private var isReverse = false
    private var isVibrate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zikir)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
        }

        binding.add.setOnClickListener {
            if (currentCounter < maxCounter || maxCounter == 0) {
                setCounter()
            } else {
                if (isReverse) {
                    currentCounter = 0
                    binding.tvCounter.text = currentCounter.toString()
                }
            }
        }

        binding.btnReset.setOnClickListener {
            currentCounter = 0
            binding.tvCounter.text = currentCounter.toString()
        }

        binding.tvMaxCounter.setOnClickListener {
            fragmentBottomSheetInput.show(supportFragmentManager, "FragmentBottomSheetInput")
        }

        binding.btnReverse.setOnClickListener {
            if (!isReverse) {
                isReverse = true
                if (currentCounter == maxCounter) {
                    currentCounter = 0
                    binding.tvCounter.text = currentCounter.toString()
                }
                binding.btnReverse.imageTintList = ColorStateList.valueOf(Color.BLACK)
            } else {
                isReverse = false
                binding.btnReverse.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.grey, theme))
            }
            preference.tasbihReverse = isReverse
        }
        binding.btnVibrate.setOnClickListener {
            if (!isVibrate) {
                isVibrate = true
                binding.btnVibrate.imageTintList = ColorStateList.valueOf(Color.BLACK)
            } else {
                isVibrate = false
                binding.btnVibrate.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.grey, theme))
            }
            preference.tasbihVibrate = isVibrate
        }

        initData()
        handleIntent(intent)
    }

    private fun setCounter() {
        GlobalScope.launch(Dispatchers.Default) {
            delay(200)
            GlobalScope.launch(Dispatchers.Main) {
                currentCounter += 1
                binding.tvCounter.text = currentCounter.toString()
                if (isVibrate) vibrate()
            }
        }
    }

    fun initData() {
        isVibrate = preference.tasbihVibrate
        isReverse = preference.tasbihReverse
        maxCounter = preference.tasbihMaxCounter

        binding.btnReverse.imageTintList = if (isReverse)
            ColorStateList.valueOf(Color.BLACK)
        else ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.grey, theme))

        binding.btnVibrate.imageTintList = if (isVibrate)
            ColorStateList.valueOf(Color.BLACK)
        else ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.grey, theme))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        intent.getIntExtra(EXTRA_CURRENT_COUNTER, currentCounter).let {
            binding.tvCounter.text = it.toString()
            currentCounter = it
        }
        intent.getIntExtra(EXTRA_CURRENT_COUNTER, maxCounter).let {
            binding.tvMaxCounter.text = if (it == 0) "/*" else "/$it"
            maxCounter = it
        }
    }

    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.EFFECT_CLICK))
            } else {
                v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } else {
            v.vibrate(80)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        if (currentCounter > 0) notification()
    }

    private fun notification() {
        val message = getString(R.string.notification_tashbih, currentCounter.toString())
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.title_tasbih_counter))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        Intent(this, ZikirActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(EXTRA_CURRENT_COUNTER, currentCounter)
                                .putExtra(EXTRA_MAX_COUNTER, maxCounter), PendingIntent.FLAG_MUTABLE))
        val managerCompat = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(ReminderReceiver.CHANNEL_ID)
            managerCompat.createNotificationChannel(channel)
        }
        managerCompat.notify(124, builder.build())
    }
}