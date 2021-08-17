package com.jumbox.app.muslim.vo

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

data class Result<T>(
        val status: Boolean,
        val data: T
)
