package com.jumbox.app.muslim.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 16/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Parcelize
data class Kitab(
        val kitab: String,
        @SerializedName("id")
        val ids: ArrayList<Int>
        ) : Parcelable