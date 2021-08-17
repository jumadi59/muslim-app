package com.jumbox.app.muslim.ui.quran

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.viewpager.widget.ViewPager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.databinding.ActivityQuranListBinding
import com.jumbox.app.muslim.utils.hideInput
import com.jumbox.app.muslim.utils.textChangedListener

class QuranListActivity : BaseActivity<ActivityQuranListBinding, QuranViewModel>() {

    lateinit var quranPagerAdapter: QuranPagerAdapter
    override fun getLayoutId() = R.layout.activity_quran_list

    override fun getViewModelClass() =  QuranViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
        }
        quranPagerAdapter = QuranPagerAdapter( this).apply {
                binding.viewPager.adapter = this
                binding.tabs.setupWithViewPager(binding.viewPager)
            }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                quranPagerAdapter.currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })

        binding.etSearch.textChangedListener {
            updateInput()
        }

        binding.etSearch.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
               updateInput()
                binding.etSearch.hideInput(view.windowToken)
                true
            } else {
                false
            }
        }

        binding.etSearch.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateInput()
                binding.etSearch.hideInput(view.windowToken)
                true
            } else {
                false
            }
        }
    }

    private fun updateInput() {
        binding.etSearch.text.let {
            if (!it.isNullOrEmpty()) {
                quranPagerAdapter.search(it.toString())
            } else {
                quranPagerAdapter.reload()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
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

    fun appbar() = binding.appBar

}