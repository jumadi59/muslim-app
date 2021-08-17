package com.jumbox.app.muslim.vo

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 13/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
@Entity(tableName = "bookmark")
@Parcelize
data class Bookmark(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val number: Int,
        val name: String,
        @Embedded(prefix = "trans_")
        val translations: Translations,
        val unicode: String,
        val verse: Int
) : Parcelable
