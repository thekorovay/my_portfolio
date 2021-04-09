package com.thekorovay.myportfolio.module_profile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.thekorovay.myportfolio.module_profile.firebase.EasyFirebase
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

    fun signUpWithGoogle() {
        EasyFirebase.signUpWithGoogle()
    }

    fun setErrorMessageDisplayed() {
        EasyFirebase.flushErrorState()
    }
}