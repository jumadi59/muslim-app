package com.jumbox.app.muslim.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by Jumadi Janjaya date on 09/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class DateHijri(private var date: Date = Date()) {

    constructor(localDate: LocalDate) : this(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))

    companion object {
        private val wdNames = arrayOf("al-ʾAḥad", "al-Ithnayn", "ath-Thulāthāʾ", "al-ʾArbiʿāʾ", "al-Khamīs",
                "al-Jumʿah", "as-Sabt")
        private val iMonthNames = arrayOf("Muḥarram", "Safar", "Rabi'ul Awwal",
                "Rabi'ul Akhir", "Jumadal Ula", "Jumadal Akhira", "Rajab",
                "Sya’ban", "Ramadan", "Shawwal", "Dhul Qa'ada", "Dhul Hijja")

        fun getDisplayName(id: Int, idIndex: Int) : String {

            return when(id) {
                Calendar.MONTH -> iMonthNames[idIndex-1]
                Calendar.DAY_OF_WEEK -> wdNames[idIndex-1]
                else -> ""
            }
        }

        fun getLocalDate(formatter: Formatter) : LocalDate {
            val hijrahDate = HijrahDate.of(formatter.year, formatter.month, formatter.day)
            return LocalDate.from(hijrahDate)
        }
    }

    fun writeIslamicDate(): Formatter {
        val iDate = hijrahDate("e-dd-MM-yyyy").split("-")
        return Formatter(
                dayName = getDisplayName(Calendar.DAY_OF_WEEK, iDate[0].toInt()),
                day = iDate[1].toInt(),
                monthName = getDisplayName(Calendar.MONTH, iDate[2].toInt()),
                month = iDate[2].toInt(),
                year = iDate[3].toInt()
        )
    }

    fun hijrahDate(pattern: String? = null) : String {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern?:"dd MMMM, yyyy")
        val zoneDateTime = ZonedDateTime.of(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), ZoneId.systemDefault())
        val hijrahDate = HijrahDate.from(zoneDateTime)
        return hijrahDate.format(dateFormatter)
    }

   data class Formatter(
           val dayName: String,
           val day: Int,
           val monthName: String,
           val month: Int,
           val year: Int
   )
}