package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel: ViewModel() {
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    val isSignedIn: LiveData<Boolean> = _isSignedIn

    fun signIn(email: String, password: String) {

    }

    fun signInWithGoogle() {

    }
}