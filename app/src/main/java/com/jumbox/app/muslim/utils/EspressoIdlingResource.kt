package com.jumbox.app.muslim.utils

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Created by Jumadi Janjaya date on 07/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"
    private val countingIdlingResource = CountingIdlingResource(RESOURCE)

    val idlingResource: IdlingResource
        get() = countingIdlingResource

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        countingIdlingResource.decrement()
    }
}