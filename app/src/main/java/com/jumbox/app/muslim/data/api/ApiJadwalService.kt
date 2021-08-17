package com.jumbox.app.muslim.data.api

import com.jumbox.app.muslim.vo.Result
import com.jumbox.app.muslim.vo.TimeShalat
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
interface ApiJadwalService {

    @GET("sholat/jadwal/{location}/{year}/{moon}")
    suspend fun prayerMoon(
        @Path("location") location: String,
        @Path("year") year: String,
        @Path("moon") moon: String,) : Response<Result<TimeShalat>>

}