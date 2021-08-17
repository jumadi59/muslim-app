package com.jumbox.app.muslim.vo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/


@Entity(tableName = "prayer")
@Parcelize
data class Prayer(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        val time: Long,
        val name: String,
        val backgroundColor: Int,
        val date: String,
        val type: String,
        @Ignore
        val alarm: Boolean = false,
) : Parcelable {

    constructor(time: Long, name: String, backgroundColor: Int, date: String, type: String) : this(0, time, name, backgroundColor, date, type, false)
}

