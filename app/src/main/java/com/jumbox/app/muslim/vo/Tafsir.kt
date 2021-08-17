package com.jumbox.app.muslim.vo

import com.google.gson.annotations.SerializedName

/**
 * Created by Jumadi Janjaya date on 12/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
data class Tafsir(
    val number: Int,
    @SerializedName("text")
    val tafsir: String,
)
