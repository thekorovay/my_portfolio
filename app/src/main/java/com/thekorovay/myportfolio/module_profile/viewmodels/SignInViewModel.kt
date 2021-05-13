package com.thekorovay.myportfolio.module_profile.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception
import javax.inject.Inject

class SignInViewModel @Inject constructor(private val firebase: EasyFirebase): ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val user = firebase.user
    val state = firebase.state

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