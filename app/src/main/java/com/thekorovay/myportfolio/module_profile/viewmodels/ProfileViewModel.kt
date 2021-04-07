package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    val isSignedIn: LiveData<Boolean> = _isSignedIn

    fun swap() {
        _isSignedIn.value = !_isSignedIn.value!!
    }
}