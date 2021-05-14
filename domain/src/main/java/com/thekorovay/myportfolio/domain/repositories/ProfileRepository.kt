package com.thekorovay.myportfolio.domain.repositories

import com.thekorovay.myportfolio.domain.entities.ProfileState
import com.thekorovay.myportfolio.domain.entities.User
import kotlinx.coroutines.flow.StateFlow
import java.lang.Exception

interface ProfileRepository {
    var exception: Exception?

    val profileState: StateFlow<ProfileState>
    
    val user: StateFlow<User?>

    val userChanged: StateFlow<Boolean>
    
    val restorePasswordEmailSent: StateFlow<Boolean>
    
    fun sendRestorePasswordEmail(email: String)

    fun getDataForGoogleSignIn(webClientId: String): Any?
    fun signInWithGoogle(data: Any?)
    fun signIn(email: String, password: String)
    fun signOut()
    fun signUp(
        email: String,
        password: String,
        repeatPassword: String,
        name: String?
    )

    fun setErrorHandled()
    fun setRestorePasswordMessageHandled()
    fun setUserChangeHandled()
}