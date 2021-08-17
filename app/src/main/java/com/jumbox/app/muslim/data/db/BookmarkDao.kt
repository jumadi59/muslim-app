package com.jumbox.app.muslim.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jumbox.app.muslim.vo.Bookmark

/**
 * Created by Jumadi Janjaya date on 18/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmark")
    fun getAll(): LiveData<List<Bookmark>>

    @Query("SELECT * FROM bookmark WHERE number=:surah")
    fun getWithSurah(surah: Int): LiveData<List<Bookmark>>

    @Query("SELECT * FROM bookmark WHERE number=:surah AND verse=:verse")
    fun get(surah: Int, verse: Int): Bookmark?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: Bookmark)

    @Delete
    suspend fun delete(bookmark: Bookmark)

    @Query("DELETE FROM bookmark WHERE number=:surah AND verse=:verse")
    suspend fun delete(surah: Int, verse: Int)
}