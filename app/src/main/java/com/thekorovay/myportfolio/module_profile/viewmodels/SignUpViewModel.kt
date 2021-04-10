package com.thekorovay.myportfolio.module_profile.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.thekorovay.myportfolio.firebase.EasyFirebase
import java.lang.Exception

class SignUpViewModel(application: Application): AndroidViewModel(application) {
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    val repeatPassword = MutableLiveData<String>("")
    val name = MutableLiveData<String>("")

    val user: LiveData<FirebaseUser?> = EasyFirebase.user

    val state: LiveData<EasyFirebase.State> = EasyFirebase.state

    val exception: Exception? get() = EasyFirebase.exception

    fun signUp() {
        EasyFirebase.signUp(
            getApplication(),
            email.value,
            password.value,
            repeatPassword.value,
            name.value
        )
    }

    fun getGoogleSignInIntent(activityContext: Context) = EasyFirebase.getGoogleSignInIntent(activityContext)

    fun signUpWithGoogle(data: Intent?) {
        data?.let { EasyFirebase.signInWithGoogle(it) }
    }

    fun setErrorMessageDisplayed() {
        EasyFirebase.flushErrorState()
    }
}