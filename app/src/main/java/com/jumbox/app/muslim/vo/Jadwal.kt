package com.jumbox.app.muslim.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Parcelize
data class Jadwal(
    val date: String,
    val imsak: String,
    val subuh: String,
    val terbit: String,
    val dhuha: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String,
) : Parcelable
