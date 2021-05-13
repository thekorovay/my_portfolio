package com.thekorovay.myportfolio.network

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import com.thekorovay.myportfolio.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EasyFirebase @Inject constructor(private val validator: Validator) {

    private val auth: FirebaseAuth = Firebase.auth.apply {
        addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser

            if (newUser != null) {
                // User signed in...
                listenToHistory()

                if (user.value == null) {
                    // ...just now
                    _userChanged.value = true
                }
            } else if (user.value != null) {
                // User signed out
                stopListeningHistory()
                _userChanged.value = true
            }

            _user.value = newUser
        }
    }

    private var historyListener: ValueEventListener? = null

    private val _user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _userChanged: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val userChanged: StateFlow<Boolean> = _userChanged

    private val _state = MutableStateFlow(State.IDLE)
    val state: StateFlow<State> = _state

    private val _restorePasswordEmailSent = MutableStateFlow(false)
    val restorePasswordEmailSent: StateFlow<Boolean> = _restorePasswordEmailSent

    private val _searchHistory = MutableStateFlow<List<FirebaseSearchRequest>>(listOf())
    val searchHistory: StateFlow<List<FirebaseSearchRequest>> = _searchHistory

    var exception: Exception? = null
        private set(value) {
            field = value
            // Auto set the ERROR state if there's an exception
            if (value != null) {
                _state.value = State.ERROR
            }
        }


    /*  SIGN IN / SIGN UP  */

    fun signUp(
        email: String?,
        password: String?,
        repeatPassword: String?,
        name: String?
    ) {
        _state.value = State.BUSY

        exception = validator.validate(email, password, repeatPassword)
        if (exception != null) {
            return
        }

        val authTask = auth.createUserWithEmailAndPassword(email!!, password!!)
        authTask.addOnCompleteListener(getOnAuthListener(name))
    }

    fun signIn(email: String?, password: String?) {
        _state.value = State.BUSY

        exception = validator.validate(email, password)
        if (exception != null) {
            return
        }

        val authTask = auth.signInWithEmailAndPassword(email!!, password!!)
        authTask.addOnCompleteListener(getOnAuthListener())
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
            val authTask = auth.signInWithCredential(credential)
            authTask.addOnCompleteListener(getOnAuthListener())
        }
    }

    fun signOut() = auth.currentUser?.run { auth.signOut() }

    private fun getOnAuthListener(userName: String? = null) = OnCompleteListener<AuthResult> { finishedTask ->
        if (finishedTask.isSuccessful) {
            _state.value = State.IDLE
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

    fun setUserChangeHandled() {
        _userChanged.value = false
    }


    /*  RESTORE PASSWORD  */

    fun sendRestorePasswordEmail(email: String?) {
        _state.value = State.BUSY

        exception = validator.validateEmail(email)
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


    /*  SEARCH HISTORY  */

    private fun getHistoryReference(userId: String) = Firebase.database.reference
            .child("users")
            .child(userId)
            .child("search_history")

    private fun listenToHistory() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            _state.value = State.BUSY

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val history = mutableListOf<FirebaseSearchRequest>()

                    snapshot.children.reversed().forEach { entrySnap ->
                        entrySnap.getValue<FirebaseSearchRequest>()?.let {
                            history.add(it)
                        }
                    }

                    _searchHistory.value = history
                    _state.value = State.IDLE
                }

                @Suppress("ThrowableNotThrown")
                override fun onCancelled(error: DatabaseError) {
                    exception = error.toException()
                }
            }

            historyListener = getHistoryReference(currentUser.uid).addValueEventListener(listener)
        }
    }

    private fun stopListeningHistory() {
        val currentUser = user.value
        val listener = historyListener

        if (currentUser != null && listener != null) {
            getHistoryReference(currentUser.uid).removeEventListener(listener)
            historyListener = null
        }
    }

    fun updateSearchHistory(request: FirebaseSearchRequest) {
        val currentUser = auth.currentUser ?: return

        _state.value = State.BUSY

        getHistoryReference(currentUser.uid)
                .push()
                .setValue(request)
                .addOnCompleteListener(getOnCompleteListener())
    }

    fun clearSearchHistory() {
        val currentUser = auth.currentUser ?: return

        _state.value = State.BUSY

        getHistoryReference(currentUser.uid).setValue(null)
                .addOnCompleteListener(getOnCompleteListener())
    }

    private fun getOnCompleteListener() = OnCompleteListener<Void> { finishedTask ->
        if (finishedTask.isSuccessful) {
            _state.value = State.IDLE
        } else {
            exception = finishedTask.exception
        }
    }


    /*  RESTORING STATE  */

    fun flushErrorState() {
        if (_state.value == State.ERROR) {
            _state.value = State.IDLE
        }
    }

    fun flushRestorePasswordEmailSent() {
        _restorePasswordEmailSent.value = false
    }


    /*  STATE  */

    enum class State {
        IDLE, BUSY, ERROR
    }
}