package com.jumbox.app.muslim.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Parcelize
data class Surah(
        @SerializedName("number_of_surah")
        val number: Int,
        val name: String,
        @SerializedName("number_of_ayah")
        val countVerse: Int,
        val place: String,
        val type: String,
        @SerializedName("name_translations")
        val translations: Translations,
        @SerializedName("ico_unicode")
        val unicode: String,
) : Parcelable
