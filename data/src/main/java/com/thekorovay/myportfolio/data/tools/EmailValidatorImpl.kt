package com.thekorovay.myportfolio.data.tools

import android.util.Patterns
import com.thekorovay.myportfolio.domain.entities.EmailValidator
import javax.inject.Singleton

@Singleton
class EmailValidatorImpl: EmailValidator {
    override fun isValidEmail(email: String?) = email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}