package com.thekorovay.myportfolio.module_profile.firebase

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
        return when {
            email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                // Invalid email
                Exception(context.getString(R.string.invalid_email_error))
            }

            password == null || password.contains(" ")
                    || password.length !in PASSWORD_MIN_LENGTH..PASSWORD_MAX_LENGTH -> {
                // Invalid password
                Exception(
                    context.getString(
                        R.string.invalid_password_error,
                        PASSWORD_MIN_LENGTH,
                        PASSWORD_MAX_LENGTH
                    )
                )
            }

            repeatPassword != password -> {
                // Passwords don't match email
                Exception(context.getString(R.string.passwords_not_match_error))
            }

            else -> null
        }
    }
}