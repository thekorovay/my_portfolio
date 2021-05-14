package com.thekorovay.myportfolio.module_profile.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.domain.interactors.ProfileInteractor
import com.thekorovay.myportfolio.entities.UIUser
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class SignUpViewModel @Inject constructor(private val interactor: ProfileInteractor): ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val repeatPassword = MutableLiveData("")
    val name = MutableLiveData("")

    val user = interactor.user.map { UIUser.fromUser(it) }
    val state = interactor.profileState

    val exception: Exception? get() = interactor.exception

    fun signUp() {
        interactor.signUp(
            email.value,
            password.value,
            repeatPassword.value,
            name.value
        )
    }

    fun getGoogleSignInIntent(activityContext: Context): Intent {
        val clientId = activityContext.getString(R.string.default_web_client_id)
        return interactor.getDataForGoogleSignIn(clientId) as Intent
    }

    fun signUpWithGoogle(data: Intent?) {
        data?.let { interactor.signInWithGoogle(it) }
    }

    fun setErrorMessageDisplayed() {
        interactor.setErrorHandled()
    }
}