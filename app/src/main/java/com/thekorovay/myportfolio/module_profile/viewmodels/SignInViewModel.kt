package com.thekorovay.myportfolio.module_profile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.thekorovay.myportfolio.module_profile.firebase.EasyFirebase
import java.lang.Exception

class SignInViewModel(application: Application): AndroidViewModel(application) {
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    val user: LiveData<FirebaseUser?> = EasyFirebase.user

    val state: LiveData<EasyFirebase.State> = EasyFirebase.state

    val exception: Exception? get() = EasyFirebase.exception

    private val _navigateToRestorePassword = MutableLiveData(false)
    val navigateToRestorePassword: LiveData<Boolean> = _navigateToRestorePassword

    fun restorePassword() {
        _navigateToRestorePassword.value = true
    }

    fun setNavigationCompleted() {
        _navigateToRestorePassword.value = false
    }

    fun signIn() {
        EasyFirebase.signIn(getApplication(), email.value, password.value)
    }

    fun signInWithGoogle() {
        EasyFirebase.signInWithGoogle()
    }

    fun setErrorMessageDisplayed() {
        EasyFirebase.flushErrorState()
    }
}