package com.jumbox.app.muslim.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Parcelize
data class Verse(
        val index: Int = 0,
        val surah: Int,

        val number: Int,
        val juz: Int,
        val page: Int,
        val text: String,
        val latin: String,
        val translations: Translations,
        val bookmark: Boolean = false
) : Parcelable
