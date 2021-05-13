package com.thekorovay.myportfolio.di

import com.thekorovay.myportfolio.module_news.ui.SearchParamsFragment
import com.thekorovay.myportfolio.module_news.ui.SearchResultsFragment
import com.thekorovay.myportfolio.module_profile.ui.ProfileFragment
import com.thekorovay.myportfolio.module_profile.ui.RestorePasswordFragment
import com.thekorovay.myportfolio.module_profile.ui.SignInFragment
import com.thekorovay.myportfolio.module_profile.ui.SignUpFragment
import com.thekorovay.myportfolio.module_search_history.ui.SearchHistoryFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: RestorePasswordFragment)
    fun inject(fragment: SignInFragment)
    fun inject(fragment: SignUpFragment)
    fun inject(fragment: SearchHistoryFragment)
    fun inject(fragment: SearchResultsFragment)
    fun inject(fragment: SearchParamsFragment)
}