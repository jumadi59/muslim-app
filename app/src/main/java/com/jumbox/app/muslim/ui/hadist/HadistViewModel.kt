package com.jumbox.app.muslim.ui.hadist

import androidx.lifecycle.*
import com.jumbox.app.muslim.data.Repository
import com.jumbox.app.muslim.vo.Hadist
import com.jumbox.app.muslim.vo.Kitab
import com.jumbox.app.muslim.vo.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Jumadi Janjaya date on 16/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class HadistViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val query = MutableLiveData<String>()
    val resposeSearch: LiveData<Resource<List<Kitab>>> = Transformations.switchMap(query) { repository.searchHadist(it) }

    private val hadist = MutableLiveData<HashMap<String, String>>()
    val resposeHadist: LiveData<Resource<Hadist>> = Transformations.switchMap(hadist) { repository.hadist(it["kitab"]!!, it["id"]!!.toInt()) }

    fun search(query: String) = viewModelScope.launch {
        this@HadistViewModel.query.postValue(query)
    }

    fun hadist(kitab: String, id: Int) = viewModelScope.launch {
        hadist.postValue(HashMap<String, String>().also {
            it["kitab"] = kitab
            it["id"] = "$id"

        })
    }

    fun retryHadist() {
        hadist.value?.let {
            hadist.value = it
        }
    }

    fun retryHSearch() {
        query.value?.let {
            query.value = it
        }
    }
}