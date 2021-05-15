package com.thekorovay.myportfolio

import android.app.Application
import com.thekorovay.myportfolio.di.AppComponent
import com.thekorovay.myportfolio.di.ContextModule
import com.thekorovay.myportfolio.di.DaggerAppComponent
import com.thekorovay.myportfolio.di.RepositoryModule

class MyApplication: Application() {
    val appComponent: AppComponent = DaggerAppComponent.builder()
        .contextModule(ContextModule(this))
        .build()
}