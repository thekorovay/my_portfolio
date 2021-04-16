package com.thekorovay.myportfolio.module_profile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception

class RestorePasswordViewModel(application: Application): AndroidViewModel(application) {
    val firebase = EasyFirebase.getInstance(application)

    val email = MutableLiveData("")

    val state: LiveData<EasyFirebase.State> = firebase.state

    val emailSent: LiveData<Boolean> = firebase.restorePasswordEmailSent

    val exception: Exception? get() = firebase.exception

    fun restore() {
        firebase.sendRestorePasswordEmail(email.value)
    }

    fun setErrorMessageDisplayed() {
        firebase.flushErrorState()
    }

    fun setSuccessMessageDisplayed() {
        firebase.flushRestorePasswordEmailSent()
    }
}