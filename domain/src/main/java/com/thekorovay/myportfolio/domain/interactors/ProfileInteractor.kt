package com.thekorovay.myportfolio.domain.interactors

import com.thekorovay.myportfolio.domain.entities.Validator
import com.thekorovay.myportfolio.domain.repositories.ProfileRepository
import java.util.HashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileInteractor @Inject constructor(
    private val repo: ProfileRepository,
    private val validator: Validator
) {
    val exception get() = repo.exception

    val profileState = repo.profileState

    val user = repo.user

    val restorePasswordEmailSent = repo.restorePasswordEmailSent

    fun sendRestorePasswordEmail(email: String?) {
        validator.validateEmail(email).let { exception ->
            if (exception != null) {
                repo.exception = exception
            } else {
                repo.sendRestorePasswordEmail(email!!)
            }
        }
    }


    fun getDataForGoogleSignIn(webClientId: String) = repo.getDataForGoogleSignIn(webClientId)

    fun signInWithGoogle(data: Any?) = repo.signInWithGoogle(data)

    fun signIn(email: String?, password: String?) {
        validator.validate(email, password).let { exception ->
            if (exception != null) {
                repo.exception = exception
            } else {
                repo.signIn(email!!, password!!)
            }
        }
    }

    fun signOut() = repo.signOut()

    fun signUp(
        email: String?,
        password: String?,
        repeatPassword: String?,
        name: String?
    ) {
        validator.validate(email, password, repeatPassword).let { exception ->
            if (exception != null) {
                repo.exception = exception
            } else {
                repo.signUp(email!!, password!!, repeatPassword!!, name)
            }
        }
    }

    fun setErrorHandled() = repo.setErrorHandled()
    fun setRestorePasswordMessageHandled() = repo.setRestorePasswordMessageHandled()
}