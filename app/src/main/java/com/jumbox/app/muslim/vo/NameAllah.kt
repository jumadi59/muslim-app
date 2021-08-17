package com.jumbox.app.muslim.vo

import com.google.gson.annotations.SerializedName

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
data class NameAllah(
        val index: Int,
        val arabic: String,
        val latin: String,
        @SerializedName("translation_id")
        val translationID: String,
        @SerializedName("translation_en")
        val translationEN: String,
        val description: String,
        @SerializedName("ico_unicode")
        val unicode: String,
        @SerializedName("timing")
        val currentTime: Long,
)
