package com.thekorovay.myportfolio.di

import android.content.Context
import com.thekorovay.myportfolio.data.R
import com.thekorovay.myportfolio.data.database.NewsDatabase
import com.thekorovay.myportfolio.data.database.getNewsDatabase
import com.thekorovay.myportfolio.data.network.NewsService
import com.thekorovay.myportfolio.data.network.getNewsApi
import com.thekorovay.myportfolio.data.repositories.ProfileRepositoryImpl
import com.thekorovay.myportfolio.data.repositories.SearchNewsRepositoryImpl
import com.thekorovay.myportfolio.data.tools.EmailValidatorImpl
import com.thekorovay.myportfolio.domain.entities.EmailValidator
import com.thekorovay.myportfolio.domain.entities.Validator
import com.thekorovay.myportfolio.domain.repositories.ProfileRepository
import com.thekorovay.myportfolio.domain.repositories.SearchNewsRepository
import dagger.Module
import dagger.Provides
import java.lang.Exception
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideNewsDatabase(context: Context) = getNewsDatabase(context)

    @Singleton
    @Provides
    fun provideNewsApi() = getNewsApi()

    @Singleton
    @Provides
    fun provideEmailValidator(): EmailValidator = EmailValidatorImpl()

    @Singleton
    @Provides
    fun provideValidator(context: Context, emailValidator: EmailValidator) = Validator(
        invalidEmailException = Exception(context.getString(R.string.invalid_email_error)),
        invalidPasswordException = Exception(context.getString(R.string.invalid_password_error)),
        invalidRepeatPasswordException = Exception(context.getString(R.string.passwords_not_match_error)),
        emailValidator = emailValidator
    )

    @Singleton
    @Provides
    fun provideProfileRepository(context: Context): ProfileRepository = ProfileRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideSearchNewsRepository(
        api: NewsService,
        db: NewsDatabase,
        profileRepo: ProfileRepository
    ): SearchNewsRepository = SearchNewsRepositoryImpl(api, db, profileRepo)
}