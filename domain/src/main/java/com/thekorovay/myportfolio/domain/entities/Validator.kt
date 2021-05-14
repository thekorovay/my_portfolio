package com.thekorovay.myportfolio.domain.entities

import java.lang.Exception
import javax.inject.Singleton

@Singleton
class Validator(
    private val invalidEmailException: Exception,
    private val invalidRepeatPasswordException: Exception,
    private val invalidPasswordException: Exception,
    private val emailValidator: EmailValidator
) {
    companion object {
        const val PASSWORD_MIN_LENGTH = 8
        const val PASSWORD_MAX_LENGTH = 20
    }

    /**
     * Validates credentials
     * @return [Exception] if there's invalid credential or null if validation passed
     */
    fun validate(
        email: String?,
        password: String?,
        repeatPassword: String? = password
    ): Exception? {
        return validateEmail(email)
            ?: validatePassword(password)
            ?: validateRepeatPassword(password, repeatPassword)
    }

    fun validateEmail(email: String?) = if (emailValidator.isValidEmail(email)) {
        null
    } else {
        invalidEmailException
    }

    fun validatePassword(password: String?) = if (password == null || password.contains(" ")
        || password.length !in PASSWORD_MIN_LENGTH..PASSWORD_MAX_LENGTH) {
        invalidPasswordException
    } else {
        null
    }

    fun validateRepeatPassword(password: String?, repeatPassword: String?) = if (repeatPassword != password) {
        invalidRepeatPasswordException
    } else {
        null
    }
}