package com.thekorovay.myportfolio.module_profile.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception

class SignUpViewModel(private val firebase: EasyFirebase): ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val repeatPassword = MutableLiveData("")
    val name = MutableLiveData("")

    val user: LiveData<FirebaseUser?> = firebase.user

    val state: LiveData<EasyFirebase.State> = firebase.state

    val exception: Exception? get() = firebase.exception

    fun signUp() {
        firebase.signUp(
            email.value,
            password.value,
            repeatPassword.value,
            name.value
        )
    }

    fun getGoogleSignInIntent(activityContext: Context) = firebase.getGoogleSignInIntent(activityContext)

    fun signUpWithGoogle(data: Intent?) {
        data?.let { firebase.signInWithGoogle(it) }
    }

    fun setErrorMessageDisplayed() {
        firebase.flushErrorState()
    }
}