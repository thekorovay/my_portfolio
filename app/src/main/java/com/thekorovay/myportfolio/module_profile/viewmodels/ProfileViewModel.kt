package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.thekorovay.myportfolio.network.EasyFirebase
import java.lang.Exception

class ProfileViewModel(private val firebase: EasyFirebase): ViewModel() {

    val user: LiveData<FirebaseUser?> = firebase.user

    val state: LiveData<EasyFirebase.State> = firebase.state

    val exception: Exception? get() = firebase.exception

    private val _navigatingTo = MutableLiveData<Directions?>()
    val navigatingTo: LiveData<Directions?> = _navigatingTo


    fun signIn() {
        _navigatingTo.value = Directions.SIGN_IN
    }

    fun signUp() {
        _navigatingTo.value = Directions.SIGN_UP
    }

    fun signOut() {
        firebase.signOut()
    }

    fun setErrorMessageDisplayed() {
        firebase.flushErrorState()
    }

    fun setNavigationCompleted() {
        _navigatingTo.value = null
    }

    enum class Directions {
        SIGN_IN, SIGN_UP
    }
}