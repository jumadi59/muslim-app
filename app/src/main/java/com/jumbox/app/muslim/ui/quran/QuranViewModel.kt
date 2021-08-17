package com.jumbox.app.muslim.ui.quran

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumbox.app.muslim.data.Repository
import com.jumbox.app.muslim.vo.Bookmark
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class QuranViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val responseListSurah = repository.listSurah()
    val responseBookmarks = repository.bookmarks()

    private val surahNumber = MutableLiveData<String>()
    val responseVerses = Transformations.switchMap(surahNumber) { repository.verseWithSurah(it) }
    val responseSurah = Transformations.switchMap(surahNumber) { repository.surah(it) }
    val responseBookmark = Transformations.switchMap(surahNumber) { repository.bookmarkWithSurah(it) }

    private val tafsir = MutableLiveData<Int>()
    val responseTafsir = Transformations.switchMap(tafsir) { repository.tafsir(it) }

    fun loadSurah(numberOrName: String) = viewModelScope.launch {
        surahNumber.postValue(numberOrName)
    }

    fun bookmark(bookmark: Bookmark) = viewModelScope.launch {
        repository.saveBookmark(bookmark)
    }

    fun deleteBookmark(surahId: Int, verseId: Int) = viewModelScope.launch  {
        repository.deleteBookmark(surahId, verseId)
    }

    fun tafsir(surah: Int) = viewModelScope.launch {
        tafsir.postValue(surah)
    }
}