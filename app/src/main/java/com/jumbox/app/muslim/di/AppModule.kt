package com.jumbox.app.muslim.di

import android.app.Application
import com.jumbox.app.muslim.data.api.ApiHadistService
import com.jumbox.app.muslim.data.api.ApiJadwalService
import com.jumbox.app.muslim.data.db.AppDatabase
import com.jumbox.app.muslim.utils.StringConverterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiJadwalService {
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
        logger.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.myquran.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiJadwalService::class.java)
    }

    @Singleton
    @Provides
    fun provideApiHadisService(): ApiHadistService {
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
        logger.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
        return Retrofit.Builder()
                .baseUrl("https://api.carihadis.com/")
                .client(client)
                .addConverterFactory(StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiHadistService::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(app: Application) : AppDatabase {
        return AppDatabase.newInstance(app)
    }
}
