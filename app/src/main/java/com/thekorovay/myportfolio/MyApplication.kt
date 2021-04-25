package com.thekorovay.myportfolio

import android.app.Application
import com.thekorovay.myportfolio.database.EasyPrefs
import com.thekorovay.myportfolio.database.getNewsDatabase
import com.thekorovay.myportfolio.module_news.viewmodels.SearchParamsViewModel
import com.thekorovay.myportfolio.module_news.viewmodels.SearchResultsViewModel
import com.thekorovay.myportfolio.module_profile.viewmodels.ProfileViewModel
import com.thekorovay.myportfolio.module_profile.viewmodels.RestorePasswordViewModel
import com.thekorovay.myportfolio.module_profile.viewmodels.SignInViewModel
import com.thekorovay.myportfolio.module_profile.viewmodels.SignUpViewModel
import com.thekorovay.myportfolio.module_search_history.viewmodel.SearchHistoryViewModel
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.network.getNewsApi
import com.thekorovay.myportfolio.repositories.SearchHistoryRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication: Application() {

    private val prefsModule = module {
        single { EasyPrefs(androidContext()) }
    }

    private val firebaseModule = module {
        single { EasyFirebase(androidContext()) }
    }

    private val newsDatabaseModule = module {
        single { getNewsDatabase(androidContext()) }
    }

    private val newsApiModule = module {
        single { getNewsApi() }
    }

    private val repoModule = module {
        factory { SearchHistoryRepository(get(), get(), get()) }
    }

    private val viewModelsModule = module {
        viewModel { SearchParamsViewModel(get(), get()) }
        viewModel { SearchResultsViewModel(get()) }
        viewModel { ProfileViewModel(get()) }
        viewModel { SignInViewModel(get()) }
        viewModel { SignUpViewModel(get()) }
        viewModel { RestorePasswordViewModel(get()) }
        viewModel { SearchHistoryViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(repoModule, newsDatabaseModule, firebaseModule, newsApiModule, prefsModule, viewModelsModule)
        }
    }
}