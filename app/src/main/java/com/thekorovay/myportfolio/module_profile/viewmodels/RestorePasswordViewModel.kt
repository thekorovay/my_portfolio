package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thekorovay.myportfolio.domain.interactors.ProfileInteractor
import java.lang.Exception
import javax.inject.Inject

class RestorePasswordViewModel @Inject constructor(private val interactor: ProfileInteractor): ViewModel() {

    val email = MutableLiveData("")

    val state = interactor.profileState

    val emailSent = interactor.restorePasswordEmailSent

    val exception: Exception? get() = interactor.exception

    fun restore() {
        interactor.sendRestorePasswordEmail(email.value)
    }

    fun setErrorMessageDisplayed() {
        interactor.setErrorHandled()
    }

    fun setSuccessMessageDisplayed() {
        interactor.setRestorePasswordMessageHandled()
    }
}