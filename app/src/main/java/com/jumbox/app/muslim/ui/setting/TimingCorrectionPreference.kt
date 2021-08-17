package com.jumbox.app.muslim.ui.setting

import android.os.Bundle
import android.view.MenuItem
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BasePreferenceActivity
import com.jumbox.app.muslim.databinding.LayoutItemPrefMoreBinding
import com.jumbox.app.muslim.databinding.PreferenceTimingCorrectionBinding
import com.jumbox.app.muslim.receiver.ReminderReceiver
import com.jumbox.app.muslim.utils.elevationAppBar

class TimingCorrectionPreference : BasePreferenceActivity<PreferenceTimingCorrectionBinding>() {


    val list: ArrayList<String> by lazy { ArrayList(preference.alarmCorrectionTime) }

    override fun getLayoutId() = R.layout.preference_timing_correction
    override fun initView() {
        setSupportActionBar(binding.layoutAppbar.toolbar)
        binding.nestedScrollView.elevationAppBar(binding.layoutAppbar.appBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
            it.setTitle(R.string.correction_timing_adzan)
        }

        binding.fajr.summary = if (list[0] == "0") getString(R.string.correct) else getString(R.string.minute, list[0])
        binding.dhuhr.summary = if (list[1] == "0") getString(R.string.correct) else getString(R.string.minute, list[1])
        binding.asr.summary = if (list[2] == "0") getString(R.string.correct) else getString(R.string.minute, list[2])
        binding.maghrib.summary = if (list[3] == "0") getString(R.string.correct) else getString(R.string.minute, list[3])
        binding.isha.summary = if (list[4] == "0") getString(R.string.correct) else getString(R.string.minute, list[4])

        binding.fajr.root.setOnClickListener {
            binding.fajr.showDialogTimer(list[0]) {
                list[0] = it
            }
        }
        binding.dhuhr.root.setOnClickListener {
            binding.dhuhr.showDialogTimer(list[1]) {
                list[1] = it
            }
        }
        binding.asr.root.setOnClickListener {
            binding.asr.showDialogTimer(list[2]) {
                list[2] = it
            }
        }
        binding.maghrib.root.setOnClickListener {
            binding.maghrib.showDialogTimer(list[3]) {
                list[3] = it
            }
        }
        binding.isha.root.setOnClickListener {
            binding.isha.showDialogTimer(list[4]) {
                list[4] = it
            }
        }
    }

    private fun LayoutItemPrefMoreBinding.showDialogTimer(current: String, callback: (value: String) -> Unit) {
        DialogTimePicker(current) {
            val value = it.split(" ")[0]
            this.tvSummary.text = if (value == "0") getString(R.string.correct) else it
            callback.invoke(value)
        }.show(this@TimingCorrectionPreference.supportFragmentManager, "${binding.root.id}_dialog")
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

    override fun initData(savedInstanceState: Bundle?) {}

    override fun onPause() {
        preference.alarmCorrectionTime = list
        super.onPause()
    }

    override fun onBackPressed() {
        if (list != preference.alarmCorrectionTime)
            ReminderReceiver.updateAlarm(this)
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (list != preference.alarmCorrectionTime)
            ReminderReceiver.updateAlarm(this)
        super.onDestroy()
    }
}