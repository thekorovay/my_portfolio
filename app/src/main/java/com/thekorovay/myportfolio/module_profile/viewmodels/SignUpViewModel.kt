package com.thekorovay.myportfolio.module_profile.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception

class SignUpViewModel(application: Application): AndroidViewModel(application) {
    val firebase = EasyFirebase.getInstance(application)
    
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    val repeatPassword = MutableLiveData<String>("")
    val name = MutableLiveData<String>("")

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