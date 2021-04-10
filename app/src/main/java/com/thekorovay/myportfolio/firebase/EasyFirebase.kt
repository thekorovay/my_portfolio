package com.thekorovay.myportfolio.firebase

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import com.thekorovay.myportfolio.R

object EasyFirebase {

    private var auth: FirebaseAuth = Firebase.auth

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _user.value = firebaseAuth.currentUser
        }
    }


    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _state = MutableLiveData(State.IDLE)
    val state: LiveData<State> = _state

    private val _restorePasswordEmailSent = MutableLiveData(false)
    val restorePasswordEmailSent: LiveData<Boolean> = _restorePasswordEmailSent

    var exception: Exception? = null
        private set(value) {
            field = value
            // Auto set the ERROR state if there's an exception
            if (value != null) {
                _state.value = State.ERROR
            }
        }


    fun signUp(
        context: Context,
        email: String?,
        password: String?,
        repeatPassword: String?,
        name: String?
    ) {
        _state.value = State.BUSY

        exception = Validator.validate(context, email, password, repeatPassword)
        if (exception != null) {
            return
        }

        auth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = State.IDLE
                    updateUserName(context, name)
                } else {
                    exception = task.exception
                }
            }
    }

    private fun updateUserName(context: Context, name: String?) {
        val sureUser = auth.currentUser!!

        // Builder() is used because handy userProfileChangeRequest { ... } lambda causes mysterious
        // 'Back-end (JVM) Internal error: Couldn't inline method call 'userProfileChangeRequest' into'
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(if (!name.isNullOrEmpty()) name else sureUser.email)
            .build()

        sureUser.updateProfile(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Notify listeners that userName has changed
                    _user.postValue(_user.value)
                } else {
                    exception = Exception(context.getString(R.string.user_name_updating_error))
                }
            }
    }

    fun signIn(context: Context, email: String?, password: String?) {
        _state.value = State.BUSY

        exception = Validator.validate(context, email, password)
        if (exception != null) {
            return
        }

        val credential = EmailAuthProvider.getCredential(email!!, password!!)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(cred: AuthCredential) {
        auth.signInWithCredential(cred)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = State.IDLE
                } else {
                    exception = task.exception
                }
            }
    }

    fun getGoogleSignInIntent(context: Context): Intent {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(context, options)

        return client.signInIntent
    }

    fun signInWithGoogle(data: Intent) {
        val taskWithAccount = GoogleSignIn.getSignedInAccountFromIntent(data)

        val account: GoogleSignInAccount? = try {
            taskWithAccount.getResult(ApiException::class.java)
        } catch (exception: ApiException) {
            null
        }

        if (account != null) {
            _state.value = State.BUSY

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            signInWithCredential(credential)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun sendRestorePasswordEmail(context: Context, email: String?) {
        _state.value = State.BUSY

        exception = Validator.validateEmail(context, email)
        if (exception != null) {
            return
        }

        Firebase.auth.sendPasswordResetEmail(email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _restorePasswordEmailSent.value = true
                        _state.value = State.IDLE
                    } else {
                        exception = task.exception
                    }
                }
    }

    fun flushErrorState() {
        if (_state.value == State.ERROR) {
            _state.value = State.IDLE
        }
    }

    fun flushRestorePasswordEmailSent() {
        _restorePasswordEmailSent.value = false
    }


    enum class State {
        IDLE, BUSY, ERROR
    }
}