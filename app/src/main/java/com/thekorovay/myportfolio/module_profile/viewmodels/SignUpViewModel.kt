package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel: ViewModel() {
    private val _isSignedUp = MutableLiveData<Boolean>(false)
    val isSignedUp: LiveData<Boolean> = _isSignedUp

    fun signUp(email: String, password: String, repeatPassword: String) {

    }

    fun signUpWithGoogle() {

    }
}