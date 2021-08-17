package com.jumbox.app.muslim.data.local

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jumbox.app.muslim.vo.*
import java.io.IOException
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
@Singleton
class LocalService @Inject constructor(private val app: Application) {

    companion object {
        private var verses: List<Verse>? = null
        private var surah: List<Surah>? = null
    }

    fun listSurah() : List<Surah> {
        if (surah == null) {
            val content = assetString("surah.json")
            surah = Gson().fromJson(content, object : TypeToken<List<Surah>>(){}.type)
        }
        return surah!!
    }

    fun surah(surah: Int) : List<Verse> {
        if (verses == null) {
            val content = assetString("al-quran.json")
            verses = Gson().fromJson(content, object : TypeToken<List<Verse>>(){}.type)
        }
        return verses!!.filter { it.surah == surah }
    }

    fun surah(surahName: String) : List<Verse> {
        if (verses == null) {
            val content = assetString("al-quran.json")
            verses = Gson().fromJson(content, object : TypeToken<List<Verse>>(){}.type)
        }
        return surah!!.run {
            verses!!.filter { it.surah == this.find { s-> s.name.contentEquals(surahName) }?.number }
        }
    }

    fun listNames() : List<NameAllah> {
        val content = assetString("asmaul-husna.json")
        return Gson().fromJson<ArrayList<NameAllah>>(content, object : TypeToken<ArrayList<NameAllah>>(){}.type)
    }

    fun tafsir(surah: Int) : Info<Tafsir> {
        val content = assetString("tafsir/${surah}.json")
        return Gson().fromJson(content, object : TypeToken<Info<Tafsir>>(){}.type)
    }

    fun citys() : List<Suggestion> {
        val content = assetString("city-indonesia.json")
        return Gson().fromJson(content, object : TypeToken<List<Suggestion>>(){}.type)
    }

    fun city(city: String) : Suggestion? {
        return citys().find { it.name == city.replace(Regex("^(Kab\\.|Kota\\.|Kota|Kabupaten)\\s"), "") }
    }

    private fun assetString(fileName: String) : String {
        return try {
            val stream = app.assets.open(fileName)
            String(stream.readBytes(), Charset.forName("UTF-8"))
        } catch (e: IOException) {
            ""
        }
    }
}