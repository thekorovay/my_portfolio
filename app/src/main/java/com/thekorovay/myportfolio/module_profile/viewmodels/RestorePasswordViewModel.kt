package com.thekorovay.myportfolio.module_profile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.thekorovay.myportfolio.module_profile.firebase.EasyFirebase
import java.lang.Exception

class RestorePasswordViewModel(application: Application): AndroidViewModel(application) {
    val email = MutableLiveData<String>("")

    val state: LiveData<EasyFirebase.State> = EasyFirebase.state

    val emailSent: LiveData<Boolean> = EasyFirebase.restorePasswordEmailSent

    val exception: Exception? get() = EasyFirebase.exception

    fun restore() {
        EasyFirebase.sendRestorePasswordEmail(getApplication(), email.value)
    }

    fun setErrorMessageDisplayed() {
        EasyFirebase.flushErrorState()
    }

    fun setSuccessMessageDisplayed() {
        EasyFirebase.flushRestorePasswordEmailSent()
    }
}