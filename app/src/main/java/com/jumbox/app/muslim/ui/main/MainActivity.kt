package com.jumbox.app.muslim.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.data.pref.Preference
import com.jumbox.app.muslim.databinding.ActivityMainBinding
import com.jumbox.app.muslim.receiver.ReminderReceiver
import com.jumbox.app.muslim.ui.asmaulhusna.AsmaulHusnaActivity
import com.jumbox.app.muslim.ui.calendar.CalendarActivity
import com.jumbox.app.muslim.ui.hadist.HadistListActivity
import com.jumbox.app.muslim.ui.kiblat.KiblatActivity
import com.jumbox.app.muslim.ui.prayer.PrayerTimeActivity
import com.jumbox.app.muslim.ui.quran.QuranListActivity
import com.jumbox.app.muslim.ui.setting.SettingsActivity
import com.jumbox.app.muslim.ui.zikir.ZikirActivity
import com.jumbox.app.muslim.utils.*
import com.jumbox.app.muslim.vo.Prayer
import com.jumbox.app.muslim.vo.Status.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private var countDownTimer: CountDownTimer? = null
    private var prayers: List<Prayer>? = null
    private var isErrorFetchLocation = false
    private val bottomSheetFindRegion = BottomSheetFindRegion{
        preference.city = it.name
        preference.locationId = it.id
        viewModel.fetchPrayers(it.id)
    }

    @Inject
    lateinit var preference: Preference

    override fun getLayoutId() = R.layout.activity_main

    override fun getViewModelClass() = MainViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        binding.nestedScrollView.elevationAppBar(binding.appBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowTitleEnabled(false)
        }

        binding.tvLocation.text = preference.city
        binding.tvLocation.setOnClickListener {
            if (isErrorFetchLocation) viewModel.fetchPrayers()
            else {
                bottomSheetFindRegion.show(supportFragmentManager, "find_region")
            }
        }

        binding.cardJadwal.setOnClickListener {
            startActivity(Intent(this, PrayerTimeActivity::class.java).putParcelableArrayListExtra(PrayerTimeActivity.EXTRA_JADWAL, ArrayList(prayers)))
        }

        binding.cardKiblat.setOnClickListener {
            startActivity(Intent(this, KiblatActivity::class.java))
        }

        binding.cardQuran.setOnClickListener {
            startActivity(Intent(this, QuranListActivity::class.java))
        }

        binding.cardHadist.setOnClickListener {
            startActivity(Intent(this, HadistListActivity::class.java))
        }

        binding.cardHasmaulHusna.setOnClickListener {
            startActivity(Intent(this, AsmaulHusnaActivity::class.java))
        }

        binding.cardCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        binding.cardZikir.setOnClickListener {
            startActivity(Intent(this, ZikirActivity::class.java))
        }

        binding.btnNotification.setOnClickListener {
            val item = prayers?.find { prayer -> prayer.time.isValid() }
            if (item != null) {
                preference.notifications.let { list ->
                    preference.notifications = if (item.alarm)  list.filter { f -> f != item.name }
                    else ArrayList<String>(list).apply { add(item.name) }
                }
                if (item.alarm) {
                    binding.btnNotification.setImageResource(if (item.type == "notify") R.drawable.ic_baseline_notifications_off_24 else R.drawable.ic_baseline_volume_off_24)
                    ReminderReceiver.updateAlarm(this)
                } else {
                    binding.btnNotification.setImageResource(if (item.type == "notify") R.drawable.ic_baseline_notifications_24 else R.drawable.ic_baseline_volume_up_24)
                    ReminderReceiver.updateAlarm(this)
                }
                viewModel.prayers()
            }
        }
        Log.d("Main", "enambe alarm ${ReminderReceiver.isReminder(this)}")

         DateHijri().apply {
             val hijri = this.writeIslamicDate()
             binding.tvMonth.text = hijri.monthName.substring(0, 3)
             binding.tvDay.text = hijri.day.toString()
         }

        //createViewAdBanner(binding.layoutAd)
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.prayers()
        viewModel.responsePrayers.observe(this) {
            it?.let { list ->
                prayers = list.run {
                    val prayers = ArrayList<Prayer>()
                    forEach { prayer ->  prayers.add(prayer.copy(alarm = preference.notifications.find { notify -> notify == prayer.name } != null)) }
                    return@run prayers
                }
                updatePrayer(prayers!!)
            }
        }

        viewModel.responseFetchPrayers.observe(this) {
            when(it.status) {
                SUCCESS -> {
                    ReminderReceiver.updateAlarm(this)
                    prayers = it.data?.run {
                        val prayers = ArrayList<Prayer>()
                        forEach { prayer ->  prayers.add(prayer.copy(alarm = preference.notifications.find { notify -> notify == prayer.name } != null)) }
                        return@run prayers
                    }
                    updatePrayer(prayers!!)
                    isErrorFetchLocation = false
                    binding.tvLocation.text = preference.city
                }
                ERROR -> {
                    binding.tvLocation.text = getString(R.string.description_error_server)
                    isErrorFetchLocation = true
                }
                LOADING -> {
                    binding.tvLocation.text = getString(R.string.loading)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.prayers()
    }

    override fun onPause() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_setting -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun updatePrayer(prayers: List<Prayer>) {
        prayers.forEach {
            if (it.time.isValid()) {
                val string = "${getStringWithNameId(it.name)} ${dateFormat("HH:mm", it.time)}"
                binding.tvTimePrayer.text = string
                uiBackground(it.backgroundColor, isNight())
                countDownTimer?.cancel()
                countDownTimer = null
                runCountDown(it.time)
                if (it.alarm)
                    binding.btnNotification.setImageResource(if (it.type == "notify") R.drawable.ic_baseline_notifications_24 else R.drawable.ic_baseline_volume_up_24)
                else binding.btnNotification.setImageResource(if (it.type == "notify") R.drawable.ic_baseline_notifications_off_24 else R.drawable.ic_baseline_volume_off_24)
                return
            }
        }

        Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DAY_OF_MONTH, 1)
            viewModel.prayers(dateFormat(time =  time.time))
        }
    }

    private fun uiBackground(color: Int, isNight: Boolean) {
        binding.tintBackgroundCard.backgroundTintList = ColorStateList.valueOf(color)
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

    private fun runCountDown(time: Long?) {
        val countTimeLong: Long
        val dNow = Date()
        if (time == null) return else {
            if (time <= dNow.time) return
            countTimeLong = time - dNow.time
        }

        if (countDownTimer != null) return

        countDownTimer = object : CountDownTimer(countTimeLong, 1000L) {
            override fun onTick(elapsedTime: Long) {
                binding.tvCountDownTimePrayer.text = createTimeString(elapsedTime)
            }

            override fun onFinish() {
                updatePrayer(prayers!!)
            }
        }
        countDownTimer!!.start()
    }

    @SuppressLint("SimpleDateFormat")
    private fun createTimeString(elapsedTime: Long): String {
        val time: String
        val df = SimpleDateFormat("HH:mm:ss")
        df.timeZone = TimeZone.getTimeZone("GMT")
        time = df.format(elapsedTime)
        return time
    }
}