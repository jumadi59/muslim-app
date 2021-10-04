package com.jumbox.app.muslim.ui.prayer

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.data.pref.Preference
import com.jumbox.app.muslim.databinding.ActivityPrayerTimeBinding
import com.jumbox.app.muslim.receiver.ReminderReceiver
import com.jumbox.app.muslim.ui.main.MainViewModel
import com.jumbox.app.muslim.utils.DateHijri
import com.jumbox.app.muslim.utils.dateFormat
import com.jumbox.app.muslim.utils.isNight
import com.jumbox.app.muslim.utils.isValid
import com.jumbox.app.muslim.vo.Prayer
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class PrayerTimeActivity : BaseActivity<ActivityPrayerTimeBinding, MainViewModel>() {

    companion object {
        const val EXTRA_JADWAL = "extra_jadwal"
    }

    @Inject
    lateinit var preference: Preference
    private var prayers: List<Prayer>? = null
    private val prayAdapter: PrayAdapter by lazy { PrayAdapter{
        preference.notifications.let { list ->
            preference.notifications = if (it.alarm)  list.filter { f -> f != it.name }
            else ArrayList<String>(list).apply { add(it.name) }
            prayAdapter.update(prayAdapter.getPosition(it), it.copy(alarm = !it.alarm))
            ReminderReceiver.updateAlarm(this)
        }
    } }

    override fun getLayoutId() = R.layout.activity_prayer_time

    override fun getViewModelClass() = MainViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        binding.rvJadwal.apply {
            layoutManager = LinearLayoutManager(
                    this@PrayerTimeActivity,
                    LinearLayoutManager.VERTICAL,
                    false
            )
            adapter = prayAdapter
        }
        uiDate()
    }

    override fun handleIntent(intent: Intent?) {
        intent?.getParcelableArrayListExtra<Prayer>(EXTRA_JADWAL)?.let {
            prayers = it
            updateUiPrayer(it)
        }

        intent?.getLongExtra(EXTRA_PRAYER_NOW, 0L)?.let {
            if (it > 0L) {
                with(AdzanDialogFragment(it)) {
                    show(supportFragmentManager, "adzan_dialog")
                }
            }
        }
        viewModel.prayers()
    }

    private fun nowTimePrayer(callbackFinish: () -> Unit) {
        supportActionBar?.setTitle(R.string.now_time_player)
        val colorTo = binding.tvTime.currentTextColor
        ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, colorTo).apply {
            duration = 500L
            repeatCount = 10000 / duration.toInt()
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                if (it.animatedValue is Int) binding.tvTime.setTextColor(it.animatedValue as Int)
            }
            this.doOnEnd {
                callbackFinish.invoke()
                supportActionBar?.setTitle(R.string.next_prayer_time)
            }
            start()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.responsePrayers.observe(this) {
            it?.let { list ->
                prayers = list.run {
                    val prayers = ArrayList<Prayer>()
                    forEach { prayer ->  prayers.add(prayer.copy(alarm = preference.notifications.find { notify -> notify == prayer.name } != null)) }
                    return@run prayers
                }
                updateUiPrayer(prayers!!)
            }
        }
        Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    runOnUiThread {
                        binding.tvTime.text = dateFormat("HH:mm:ss", Date().time)
                    }
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        viewModel.prayers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.menu_prayer_time, menu)
        return true
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

    private fun updateUiPrayer(prayers: List<Prayer>) {
        var currentIndex = -2
        find@ for ((index, prayer) in prayers.withIndex()) {
            if (prayer.time.isValid()) {
                uiBackground(prayer.backgroundColor, isNight())
                currentIndex = index
                break@find
            }
        }
        if (currentIndex == -2) {
            Calendar.getInstance().apply {
                time = Date()
                add(Calendar.DAY_OF_MONTH, 1)
                viewModel.prayers(dateFormat(time = time.time))
            }
        } else {
            prayAdapter.currentIndex = currentIndex
            prayAdapter.submitList(prayers)
            binding.rvJadwal.smoothScrollToPosition(currentIndex)
        }
    }

    private fun uiDate() {
        binding.tvCity.text = preference.city
        binding.tvDateHijri.let {
            val hijri = DateHijri().writeIslamicDate()
            val hijriDate = "${hijri.day} ${hijri.monthName}, ${hijri.year} H"
            it.text = hijriDate
            binding.tvDay.text = hijri.dayName
        }
    }

    private fun uiBackground(color: Int, isNight: Boolean) {
        binding.root.backgroundTintList = ColorStateList.valueOf(color)
        binding.imgTintMosque.imageTintList = ColorStateList.valueOf(color)
        binding.rvJadwal.backgroundTintList = ColorStateList.valueOf(color)
        window.statusBarColor = color
        if (isNight) {
            binding.imgLighting.setImageResource(R.drawable.ic_light)
            binding.imgSunMoon.setImageResource(R.drawable.ic_moon)
            binding.imgSunMoon.imageTintList = ColorStateList.valueOf(Color.WHITE)
            binding.imgLighting.imageTintList = ColorStateList.valueOf(Color.WHITE)
        } else {
            binding.imgLighting.setImageResource(R.drawable.ic_light2)
            binding.imgSunMoon.setImageResource(R.drawable.ic_sun)
            binding.imgSunMoon.imageTintList = ColorStateList.valueOf(Color.parseColor("#eeee8e"))
            binding.imgLighting.imageTintList = ColorStateList.valueOf(Color.parseColor("#eeee8e"))
        }
    }
}