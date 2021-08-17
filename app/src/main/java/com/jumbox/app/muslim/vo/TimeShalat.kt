package com.jumbox.app.muslim.vo

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
data class TimeShalat(
    val id: String,
    val lokasi: String,
    val daerah: String,
    val koordinat: Koordinat,
    val jadwal: List<Jadwal>
)