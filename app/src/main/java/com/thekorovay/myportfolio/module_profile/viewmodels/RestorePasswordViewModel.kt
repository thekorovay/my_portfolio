package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception

class RestorePasswordViewModel(private val firebase: EasyFirebase): ViewModel() {

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