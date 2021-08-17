package com.jumbox.app.muslim.vo

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
data class Results<T>(
    val status: Boolean,
    val data: List<T>
)