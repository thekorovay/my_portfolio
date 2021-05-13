package com.thekorovay.myportfolio.di

import android.content.Context
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.network.getNewsApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideSharedPrefs(context: Context) = context.getSharedPreferences("default_prefs", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideNewsDatabase(context: Context) = getNewsDatabase(context)

    @Singleton
    @Provides
    fun provideNewsApi() = getNewsApi()
}