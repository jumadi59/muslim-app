package com.jumbox.app.muslim.data

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.jumbox.app.muslim.data.api.ApiHadistService
import com.jumbox.app.muslim.data.api.ApiJadwalService
import com.jumbox.app.muslim.data.db.AppDatabase
import com.jumbox.app.muslim.data.local.LocalService
import com.jumbox.app.muslim.data.pref.Preference
import com.jumbox.app.muslim.utils.PrayerUtil
import com.jumbox.app.muslim.utils.convertToList
import com.jumbox.app.muslim.utils.dateFormat
import com.jumbox.app.muslim.vo.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.Collections.emptyList
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class Repository @Inject constructor(
        private val apiService: ApiJadwalService,
        private val appDatabase: AppDatabase,
        private val preference: Preference,
        private val localService: LocalService,
        private val apiHadistService: ApiHadistService
) {

    fun region() : LiveData<List<Suggestion>> {
        val result = MutableLiveData<List<Suggestion>>()
        result.postValue(localService.citys())
        return result
    }

    fun findRegions(city: String) : LiveData<Suggestion?> {
        val result = MutableLiveData<Suggestion?>()
        result.postValue(localService.city(city))
        return result

    }

    fun prayer(date: String) : LiveData<List<Prayer>> {
        val results = MediatorLiveData<List<Prayer>>()
        GlobalScope.launch(Dispatchers.IO) {
            val list = prayerListFromDay(if(date.isEmpty()) dateFormat("yyyy-MM-dd") else date)
            GlobalScope.launch(Dispatchers.Main) {
                results.postValue(list)
            }
        }
        return results
    }

    private suspend fun prayerListFromDay(date: String) : List<Prayer> {
        val list = appDatabase.prayerDao().get(dateFormat(date))
        return if (list.isEmpty()) {
            val nextDay = dateFormat("yyyy-MM-dd", Date().time + 1000 * 60 * 60 * 24).split("-")
            val nowDay = date.split("-")
            val prayers = loadApiPrayer(when {
                nextDay[0] > nowDay[0] -> nextDay.joinToString("-")
                nextDay[1] > nowDay[1] -> nextDay.joinToString("-")
                else -> date
            } )
            PrayerUtil(preference).correctionTimingPrayers(prayers?: emptyList())
        } else PrayerUtil(preference).correctionTimingPrayers(list)
    }

    fun fetchReminderPrays(idCity: String) : LiveData<Resource<List<Prayer>>> {
        val results = MutableLiveData<Resource<List<Prayer>>>()
        val city = localService.citys().find { it.id == idCity }!!
        results.postValue(Resource.loading())
        GlobalScope.launch(Dispatchers.IO) {
            val dates = dateFormat("yyyy-MM-dd").split("-")
            try {
                val response = apiService.prayerMoon(city.id, dates[0], dates[1])
                val data = response.body()?.data
                val list = ArrayList<Prayer>()
                data?.let {
                    it.jadwal.forEach { jadwal ->
                        list.addAll(jadwal.convertToList())
                    }
                }
                preference.locationId = city.id
                preference.city = city.name

                appDatabase.prayerDao().deleteAll()
                appDatabase.prayerDao().insertAll(list)
                val prayers = list.filter { it.date == dates.joinToString("-") }
                results.postValue(Resource.success(prayers))
            } catch (e: Exception) {
                results.postValue(Resource.error("Error"))
            }
        }
        return results
    }

    suspend fun loadApiPrayer(date: String) : List<Prayer>? {
        val dates = date.split("-")
        return try {
            val response = apiService.prayerMoon(preference.locationId?:"", dates[0], dates[1])
            val data = response.body()?.data
            val list = ArrayList<Prayer>()
            data?.let {
                it.jadwal.forEach { jadwal ->
                    list.addAll(jadwal.convertToList())
                }
            }
            appDatabase.prayerDao().deleteAll()
            appDatabase.prayerDao().insertAll(list)
            list.filter { it.date == date }
        } catch (e: Exception) {
            null

        }
    }

    fun listSurah() : LiveData<List<Surah>> {
        val results = MutableLiveData<List<Surah>>()
        results.postValue(localService.listSurah())
        return results
    }

    fun surah(numberOrName: String) : LiveData<Surah> {
        val result = MutableLiveData<Surah>()
        GlobalScope.launch(Dispatchers.IO) {
            val surah = localService.listSurah()
            if (TextUtils.isDigitsOnly(numberOrName))
                result.postValue(surah.find { it.number == numberOrName.toInt() })
            else
                result.postValue(surah.find { it.name.contentEquals(numberOrName) })
        }
        return result
    }

    fun verseWithSurah(surah: String) : LiveData<List<Verse>> {
        val results = MutableLiveData<List<Verse>>()
        GlobalScope.launch(Dispatchers.IO) {
            if (TextUtils.isDigitsOnly(surah))
                results.postValue(localService.surah(surah.toInt()))
            else
                results.postValue(localService.surah(surah))
        }
        return results
    }


    fun listNames() : LiveData<List<NameAllah>> {
        val  results = MutableLiveData<List<NameAllah>>()
        results.postValue(localService.listNames())
        return results
    }


    fun bookmarks()  = appDatabase.bookmarkDao().getAll()

    fun bookmarkWithSurah(numberOrName: String): LiveData<List<Bookmark>> {
        return if (TextUtils.isDigitsOnly(numberOrName))
            appDatabase.bookmarkDao().getWithSurah(numberOrName.toInt())
        else {
            val surah = localService.listSurah().find { it.name.contentEquals(numberOrName) }
            appDatabase.bookmarkDao().getWithSurah(surah?.number?:0)
        }
    }

    fun saveBookmark(bookmark: Bookmark) {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.bookmarkDao().insert(bookmark)
        }
    }

    fun deleteBookmark(surahId: Int, verseId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.bookmarkDao().delete(surahId, verseId)
        }
    }

    fun tafsir(surah: Int) : LiveData<Info<Tafsir>> {
        val result = MutableLiveData<Info<Tafsir>>()
        result.postValue(localService.tafsir(surah))
        return result
    }

    fun searchHadist(query: String) : LiveData<Resource<List<Kitab>>> {
        val results = MutableLiveData<Resource<List<Kitab>>>()
        results.postValue(Resource.loading())
        apiHadistService.search(query).enqueue(object : Callback<Results<Kitab>> {
            override fun onResponse(call: Call<Results<Kitab>>, response: Response<Results<Kitab>>) {
                val data = response.body()?.data
                if (data != null && response.isSuccessful && response.code() == 200) {
                    if (data.isEmpty())
                        results.postValue(Resource(Status.SUCCESS, response.code(), null))
                    else
                    results.postValue(Resource.success(data))
                } else
                    results.postValue(Resource(Status.ERROR, response.code(), null))
            }

            override fun onFailure(call: Call<Results<Kitab>>, t: Throwable) {
                results.postValue(Resource.error(t.message, t.hashCode()))
            }

        })
        return results
    }

    fun hadist(kitab: String, id: Int) : LiveData<Resource<Hadist>> {
        val result = MutableLiveData<Resource<Hadist>>()
        result.postValue(Resource.loading())
        apiHadistService.hadis(kitab, id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body()
                if (response.isSuccessful && !body.isNullOrEmpty() && response.code() == 200) {
                    val string = JsonParser().parse(body).asJsonObject.getAsJsonObject("data").getAsJsonObject("1")
                    val data = Gson().fromJson(string, Hadist::class.java)
                    result.postValue(Resource.success(data))
                } else {
                    result.postValue(Resource(Status.ERROR, response.code(), null))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                result.postValue(Resource.error(t.message, t.hashCode()))
            }

        })
        return result
    }



}