package com.jumbox.app.muslim.vo

import com.google.gson.annotations.SerializedName

/**
 * Created by Jumadi Janjaya date on 09/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
data class Info<T>(
        val name: String,
        val source: String,
        @SerializedName("texts")
        val list: List<T>
)