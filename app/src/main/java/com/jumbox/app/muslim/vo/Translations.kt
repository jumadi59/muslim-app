package com.jumbox.app.muslim.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 17/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Parcelize
data class Translations(
        val en: String,
        val id: String,
        val ar: String? = null
) : Parcelable