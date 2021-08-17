package com.jumbox.app.muslim.vo

import com.jumbox.app.muslim.utils.DateHijri
import java.time.LocalDate

/**
 * Created by Jumadi Janjaya date on 03/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
data class HighlightDay(
        val hijriDay: Int,
        val hijriMonth: Int,
        val title: String,
        val titleEN: String,
        val subTitle: String,
        val description: String,
        val descriptionEN: String,
        val localDate: LocalDate? = null
)
