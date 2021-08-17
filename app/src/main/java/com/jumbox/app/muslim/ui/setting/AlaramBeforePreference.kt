package com.jumbox.app.muslim.ui.setting

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.google.android.material.radiobutton.MaterialRadioButton
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BasePreferenceActivity
import com.jumbox.app.muslim.databinding.PreferenceAlaramBeforeBinding
import com.jumbox.app.muslim.receiver.ReminderReceiver
import com.jumbox.app.muslim.utils.elevationAppBar

class AlaramBeforePreference : BasePreferenceActivity<PreferenceAlaramBeforeBinding>() {

    private val currentOld: Int by lazy { preference.alarmTimeOut }

    override fun getLayoutId() = R.layout.preference_alaram_before

    override fun initView() {
        setSupportActionBar(binding.layoutAppbar.toolbar)
        binding.nestedScrollView.elevationAppBar(binding.layoutAppbar.appBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
            it.title = getString(R.string.alarm_adzan)
        }

        val list = ArrayList<Pair<Int, String>>()
        list.add(Pair(0, getString(R.string.alarm_shalat_now)))
        list.add(Pair(2, getString(R.string.alarm_before_time_salat, "2")))
        list.add(Pair(3, getString(R.string.alarm_before_time_salat, "3")))
        list.add(Pair(5, getString(R.string.alarm_before_time_salat, "5")))
        list.add(Pair(10, getString(R.string.alarm_before_time_salat, "10")))

        binding.radioGroup.apply {
            removeAllViews()
            list.forEach {
                binding.radioGroup.addView(MaterialRadioButton(this@AlaramBeforePreference, null).apply {
                    layoutParams = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutDirection = View.LAYOUT_DIRECTION_RTL
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    text = it.second
                    id = it.first
                })
            }
            check(preference.alarmTimeOut)
            setOnCheckedChangeListener { _, i ->
                list.find { it.first == i }?.let {
                    preference.alarmTimeOut = it.first
                }
            }
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

    override fun onDestroy() {
        if (currentOld != preference.alarmTimeOut)
            ReminderReceiver.updateAlarm(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (currentOld != preference.alarmTimeOut)
            ReminderReceiver.updateAlarm(this)
        super.onBackPressed()
    }

    override fun initData(savedInstanceState: Bundle?) {}
}