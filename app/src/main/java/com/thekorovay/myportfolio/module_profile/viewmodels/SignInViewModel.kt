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

class SignInViewModel(application: Application): AndroidViewModel(application) {
    val firebase = EasyFirebase.getInstance(application)

    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    val user: LiveData<FirebaseUser?> = firebase.user
    val state: LiveData<EasyFirebase.State> = firebase.state

    val exception: Exception? get() = firebase.exception

    private val _navigateToRestorePassword = MutableLiveData(false)
    val navigateToRestorePassword: LiveData<Boolean> = _navigateToRestorePassword

    fun restorePassword() {
        _navigateToRestorePassword.value = true
    }

    fun setNavigationCompleted() {
        _navigateToRestorePassword.value = false
    }

    fun signIn() {
        firebase.signIn(email.value, password.value)
    }

    fun getGoogleSignInIntent(activityContext: Context) = firebase.getGoogleSignInIntent(activityContext)

    fun signInWithGoogle(data: Intent?) {
        data?.let { firebase.signInWithGoogle(it) }
    }

    fun setErrorMessageDisplayed() {
        firebase.flushErrorState()
    }
}