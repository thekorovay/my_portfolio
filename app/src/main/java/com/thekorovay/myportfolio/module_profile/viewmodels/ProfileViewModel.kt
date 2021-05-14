package com.thekorovay.myportfolio.module_profile.viewmodels

import androidx.lifecycle.*
import com.thekorovay.myportfolio.domain.interactors.ProfileInteractor
import com.thekorovay.myportfolio.entities.UIUser
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val interactor: ProfileInteractor): ViewModel() {

    val user = interactor.user.map { UIUser.fromUser(it) }

    val state = interactor.profileState

    val exception: Exception? get() = interactor.exception

    private val _navigatingTo = MutableLiveData<Directions?>()
    val navigatingTo: LiveData<Directions?> = _navigatingTo


    fun signIn() {
        _navigatingTo.value = Directions.SIGN_IN
    }

    fun signUp() {
        _navigatingTo.value = Directions.SIGN_UP
    }

    fun signOut() {
        interactor.signOut()
    }

    fun setErrorMessageDisplayed() {
        interactor.setErrorHandled()
    }

    fun setNavigationCompleted() {
        _navigatingTo.value = null
    }

    enum class Directions {
        SIGN_IN, SIGN_UP
    }
}