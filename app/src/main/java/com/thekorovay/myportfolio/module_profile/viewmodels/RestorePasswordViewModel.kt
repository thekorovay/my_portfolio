package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception
import javax.inject.Inject

class RestorePasswordViewModel @Inject constructor(private val firebase: EasyFirebase): ViewModel() {

    val email = MutableLiveData("")

    val state = firebase.state

    val emailSent = firebase.restorePasswordEmailSent

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