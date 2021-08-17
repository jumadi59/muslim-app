package com.jumbox.app.muslim.data.api

import com.jumbox.app.muslim.vo.Kitab
import com.jumbox.app.muslim.vo.Results
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Jumadi Janjaya date on 13/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
interface ApiHadistService {

    @GET("/")
    fun search(
            @Query("q") query: String
    ) : Call<Results<Kitab>>

    @GET("/")
    fun hadis(
            @Query("kitab") kitab: String,
            @Query("id") id: Int
    ) : Call<String>
}