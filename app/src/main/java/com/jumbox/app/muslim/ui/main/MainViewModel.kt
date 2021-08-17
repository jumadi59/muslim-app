package com.jumbox.app.muslim.ui.main

import androidx.lifecycle.*
import com.jumbox.app.muslim.data.Repository
import com.jumbox.app.muslim.vo.NameAllah
import com.jumbox.app.muslim.vo.Prayer
import com.jumbox.app.muslim.vo.Suggestion
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class MainViewModel  @Inject constructor(private val repository: Repository) : ViewModel() {

    val responseRegions: LiveData<List<Suggestion>> = repository.region()
    private val region = MutableLiveData<String>()
    val responseRegion: LiveData<Suggestion?> = Transformations.switchMap(region) { repository.findRegions(it) }

    private val prayers = MutableLiveData<String>()
    val responsePrayers: LiveData<List<Prayer>> = Transformations.switchMap(prayers) { repository.prayer(it) }

    private val fetchPrayers = MutableLiveData<String>()
    val responseFetchPrayers = Transformations.switchMap(fetchPrayers) { repository.fetchReminderPrays(it) }

    private val names = MutableLiveData<Boolean>()
    val responseNames: LiveData<List<NameAllah>> = Transformations.switchMap(names) { repository.listNames() }

    fun prayers(nextDate: String = "")  = viewModelScope.launch {
        prayers.postValue(nextDate)
    }

    fun fetchPrayers(idCity: String? = null)  = viewModelScope.launch {
        if (idCity != null) fetchPrayers.postValue(idCity)
        else fetchPrayers.value?.let { fetchPrayers.value = it }
    }

    fun names()  = viewModelScope.launch {
        names.postValue(true)
    }

    fun findRegion(city: String) = viewModelScope.launch {
        region.postValue(city)
    }
}