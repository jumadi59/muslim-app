package com.jumbox.app.muslim.ui.quran

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.jumbox.app.muslim.R

/**
 * Created by Jumadi Janjaya date on 18/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class QuranPagerAdapter(private val activity: AppCompatActivity) : FragmentPagerAdapter(activity.supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = arrayOf(SurahFragment(), BookmarkFragment())
    private val pageTitles = intArrayOf(R.string.title_surah, R.string.title_bookmark)
    var currentPosition = 0

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]
    override fun getPageTitle(position: Int): CharSequence = activity.getString(pageTitles[position])

    fun search(query: String) {
        if (currentPosition >= 0) {
            if (fragments[currentPosition] is SurahFragment)
                (fragments[currentPosition] as SurahFragment).surahAdapter.search(query)
            else if (fragments[currentPosition] is BookmarkFragment)
                (fragments[currentPosition] as BookmarkFragment).bookmarkAdapter.search(query)
        }
    }

    fun reload() {
        if (currentPosition >= 0) {
            if (fragments[currentPosition] is SurahFragment)
                (fragments[currentPosition] as SurahFragment).surahAdapter.reload()
            else if (fragments[currentPosition] is BookmarkFragment)
                (fragments[currentPosition] as BookmarkFragment).bookmarkAdapter.reload()
        }
    }
}