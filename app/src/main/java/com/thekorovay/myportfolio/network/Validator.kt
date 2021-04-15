package com.thekorovay.myportfolio.network

import android.content.Context
import android.util.Patterns
import com.thekorovay.myportfolio.R
import java.lang.Exception

class Validator(context: Context) {
    private val PASSWORD_MIN_LENGTH = 8
    private val PASSWORD_MAX_LENGTH = 20

    private val invalidEmailException = Exception(context.getString(R.string.invalid_email_error))
    private val invalidRepeatPasswordException = Exception(context.getString(R.string.passwords_not_match_error))
    private val invalidPasswordException = Exception(
            context.getString(
                    R.string.invalid_password_error,
                    PASSWORD_MIN_LENGTH,
                    PASSWORD_MAX_LENGTH
            )
    )

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

    fun validateEmail(email: String?) = if (email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        invalidEmailException
    } else {
        null
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