package com.jumbox.app.muslim.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jumbox.app.muslim.vo.Bookmark
import com.jumbox.app.muslim.vo.Prayer

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Database(entities = [
    Prayer::class,
    Bookmark::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun prayerDao() : PrayerDao
    abstract fun bookmarkDao() : BookmarkDao

    companion object {
        private const val DATABASE_NAME = "app-database.db"

        fun newInstance(context: Context) : AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}