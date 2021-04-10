package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.thekorovay.myportfolio.firebase.EasyFirebase
import java.lang.Exception

class ProfileViewModel: ViewModel() {

    val user: LiveData<FirebaseUser?> = EasyFirebase.user

    val state: LiveData<EasyFirebase.State> = EasyFirebase.state

    val exception: Exception? get() = EasyFirebase.exception

    private val _navigatingTo = MutableLiveData<Directions?>()
    val navigatingTo: LiveData<Directions?> = _navigatingTo


    fun signIn() {
        if (user.value == null) {
            _navigatingTo.value = Directions.SIGN_IN
        }
    }

    fun signUp() {
        if (user.value == null) {
            _navigatingTo.value = Directions.SIGN_UP
        }
    }

    fun signOut() {
        EasyFirebase.signOut()
    }

    fun setErrorMessageDisplayed() {
        EasyFirebase.flushErrorState()
    }

    fun setNavigationCompleted() {
        _navigatingTo.value = null
    }

    enum class Directions {
        SIGN_IN, SIGN_UP
    }
}