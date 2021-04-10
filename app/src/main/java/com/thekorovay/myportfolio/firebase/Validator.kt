package com.thekorovay.myportfolio.firebase

import android.content.Context
import android.util.Patterns
import com.thekorovay.myportfolio.R
import java.lang.Exception

object Validator {
    private const val PASSWORD_MIN_LENGTH = 8
    private const val PASSWORD_MAX_LENGTH = 20

    /**
     * Validates credentials
     * @return [Exception] if there's invalid credential or null if validation passed
     */
    fun validate(
        context: Context,
        email: String?,
        password: String?,
        repeatPassword: String? = password
    ): Exception? {
        return validateEmail(context, email)
                ?: validatePassword(context, password)
                ?: validateRepeatPassword(context, password, repeatPassword)
    }

    fun validateEmail(context: Context, email: String?): Exception? {
        if (email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Exception(context.getString(R.string.invalid_email_error))
        }

        return null
    }

    fun validatePassword(context: Context, password: String?): Exception? {
        if (password == null || password.contains(" ")
                || password.length !in PASSWORD_MIN_LENGTH..PASSWORD_MAX_LENGTH) {
            return Exception(
                    context.getString(
                            R.string.invalid_password_error,
                            PASSWORD_MIN_LENGTH,
                            PASSWORD_MAX_LENGTH
                    )
            )
        }

        return null
    }

    fun validateRepeatPassword(context: Context, password: String?, repeatPassword: String?): Exception? {
        if (repeatPassword != password) {
            return Exception(context.getString(R.string.passwords_not_match_error))
        }

        return null
    }
}