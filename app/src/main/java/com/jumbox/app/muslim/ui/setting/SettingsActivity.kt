package com.jumbox.app.muslim.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import com.jumbox.app.muslim.BuildConfig
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BasePreferenceActivity
import com.jumbox.app.muslim.databinding.PreferenceSettingsBinding
import com.jumbox.app.muslim.ui.about.AboutActivity
import com.jumbox.app.muslim.utils.elevationAppBar
import com.jumbox.app.muslim.utils.gone

class SettingsActivity : BasePreferenceActivity<PreferenceSettingsBinding>() {

    override fun getLayoutId() = R.layout.preference_settings

    override fun initView() {
        setSupportActionBar(binding.layoutAppbar.toolbar)
        binding.nestedScrollView.elevationAppBar(binding.layoutAppbar.appBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
        }

        binding.alarmAdzan.root.setOnClickListener {
            startActivity(Intent(this, AlaramBeforePreference::class.java))
        }

        binding.corrountAdzan.root.setOnClickListener {
            startActivity(Intent(this, TimingCorrectionPreference::class.java))
        }

        binding.about.root.setOnClickListener  {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.rate.root.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
            })
        }

        binding.more.root.setOnClickListener  {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/dev?id=${BuildConfig.DEVELOPER_ID}")
            })
        }

        binding.donate.root.gone()
    }


    override fun onResume() {
        super.onResume()
        initData(null)
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

    override fun initData(savedInstanceState: Bundle?) {
        binding.alarmAdzan.summary = if (preference.alarmTimeOut == 0)
            getString(R.string.alarm_shalat_now) else getString(
            R.string.alarm_before_time_salat,
            " ${preference.alarmTimeOut}"
        )
        binding.corrountAdzan.summary = preference.alarmCorrectionTime.joinToString(",")
        binding.donate.summary = "Donasi untuk Developers"
    }
}