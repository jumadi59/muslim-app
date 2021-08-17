package com.jumbox.app.muslim.vo

import com.google.gson.annotations.SerializedName

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
data class Suggestion(
    val id: String,
    @SerializedName("name", alternate = ["city"])
    val name: String
)
