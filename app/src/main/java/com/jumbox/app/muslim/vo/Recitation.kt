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
data class Recitation(
    val name: String,
    @SerializedName("audio_url")
    val audioUrl: String
) : Parcelable
