package com.thekorovay.myportfolio.di

import android.content.Context
import android.content.SharedPreferences
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.network.getNewsApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideNewsDatabase(context: Context) = getNewsDatabase(context)

    @Singleton
    @Provides
    fun provideNewsApi() = getNewsApi()
}