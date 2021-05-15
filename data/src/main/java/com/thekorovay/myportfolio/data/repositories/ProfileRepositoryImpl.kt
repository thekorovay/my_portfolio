package com.thekorovay.myportfolio.data.repositories

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.thekorovay.myportfolio.domain.entities.ProfileState
import com.thekorovay.myportfolio.domain.entities.User
import com.thekorovay.myportfolio.domain.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl(
    private val appContext: Context
): ProfileRepository {

    private val auth: FirebaseAuth = Firebase.auth.apply {
        addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser

            if (newUser != null && user.value == null) {
                // User signed in just now
                _userChanged.value = true
            } else if (newUser == null && user.value != null) {
                // User signed out just now
                _userChanged.value = true
            }

            _user.value = newUser?.run { User(displayName, uid) }
        }
    }

    override var exception: Exception? = null
        set(value) {
            field = value
            value?.run { _profileState.value = ProfileState.ERROR }
        }

    private val _userChanged: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val userChanged: StateFlow<Boolean> = _userChanged
    
    private val _profileState = MutableStateFlow(ProfileState.IDLE)
    override val profileState: StateFlow<ProfileState> = _profileState

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    override val user: StateFlow<User?> = _user

    private val _restorePasswordEmailSent: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val restorePasswordEmailSent: StateFlow<Boolean> = _restorePasswordEmailSent

    override fun sendRestorePasswordEmail(email: String) {
        _profileState.value = ProfileState.BUSY

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _restorePasswordEmailSent.value = true
                    _profileState.value = ProfileState.IDLE
                } else {
                    exception = task.exception
                }
            }
    }

    override fun getDataForGoogleSignIn(webClientId: String): Intent {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(appContext, options)

        return client.signInIntent
    }

    override fun signInWithGoogle(data: Any?) {
        val intent = data as Intent

        val taskWithAccount = GoogleSignIn.getSignedInAccountFromIntent(intent)

        val account: GoogleSignInAccount? = try {
            taskWithAccount.getResult(ApiException::class.java)
        } catch (exception: ApiException) {
            null
        }

        if (account != null) {
            _profileState.value = ProfileState.BUSY

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authTask = auth.signInWithCredential(credential)
            authTask.addOnCompleteListener(getOnAuthListener())
        }
    }

    override fun signIn(email: String, password: String) {
        _profileState.value = ProfileState.BUSY 

        val authTask = auth.signInWithEmailAndPassword(email, password)
        authTask.addOnCompleteListener(getOnAuthListener())
    }

    override fun signOut() {
        auth.currentUser?.run { auth.signOut() }
    }

    override fun signUp(email: String, password: String, repeatPassword: String, name: String?) {
        _profileState.value = ProfileState.BUSY 

        val authTask = auth.createUserWithEmailAndPassword(email, password)
        authTask.addOnCompleteListener(getOnAuthListener(name))
    }

    override fun setErrorHandled() {
        if (_profileState.value == ProfileState.ERROR) {
            _profileState.value = ProfileState.IDLE
        }
    }

    override fun setRestorePasswordMessageHandled() {
        _userChanged.value = false
    }

    override fun setUserChangeHandled() {
        _userChanged.value = false
    }

    private fun getOnAuthListener(userName: String? = null) = OnCompleteListener<AuthResult> { finishedTask ->
        if (finishedTask.isSuccessful) {
            _profileState.value = ProfileState.IDLE
            if (auth.currentUser!!.displayName.isNullOrEmpty()) {
                updateUserName(userName)
            }
        } else {
            exception = finishedTask.exception
        }
    }

    private fun updateUserName(name: String?) {
        val sureUser = auth.currentUser!!

        // Builder() is used because handy userProfileChangeRequest { ... } lambda causes mysterious
        // 'Back-end (JVM) Internal error: Couldn't inline method call 'userProfileChangeRequest' into'
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(name.takeIf { !it.isNullOrEmpty() } ?: sureUser.email)
            .build()

        sureUser.updateProfile(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userChanged.value = true
                } else {
                    exception = task.exception
                }
            }
    }
}