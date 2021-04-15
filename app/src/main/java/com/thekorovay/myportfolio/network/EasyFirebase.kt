package com.thekorovay.myportfolio.network

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

class EasyFirebase private constructor(context: Context) {

    companion object {
        private lateinit var INSTANCE: EasyFirebase

        fun getInstance(context: Context): EasyFirebase {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = EasyFirebase(context)
            }

            return INSTANCE
        }
    }


    private var auth: FirebaseAuth = Firebase.auth.apply {
        addAuthStateListener { firebaseAuth ->
            _user.value = firebaseAuth.currentUser
        }
    }
    private val validator = Validator(context)


    private val _user = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val user: LiveData<FirebaseUser?> = _user

    private val _state = MutableLiveData(State.IDLE)
    val state: LiveData<State> = _state

    private val _restorePasswordEmailSent = MutableLiveData(false)
    val restorePasswordEmailSent: LiveData<Boolean> = _restorePasswordEmailSent

    private val _searchHistory = MutableLiveData<List<FirebaseSearchRequest>>()
    val searchHistory: LiveData<List<FirebaseSearchRequest>> = _searchHistory

    var exception: Exception? = null
        private set(value) {
            field = value
            // Auto set the ERROR state if there's an exception
            if (value != null) {
                _state.postValue(State.ERROR)
            }
        }


    /*  SIGN IN / SIGN UP  */

    fun signUp(
        email: String?,
        password: String?,
        repeatPassword: String?,
        name: String?
    ) {
        _state.postValue(State.BUSY)

        exception = validator.validate(email, password, repeatPassword)
        if (exception != null) {
            return
        }

        val authTask = auth.createUserWithEmailAndPassword(email!!, password!!)
        authTask.addOnCompleteListener(getOnAuthListener(name))
    }

    fun signIn(email: String?, password: String?) {
        _state.postValue(State.BUSY)

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
            _state.postValue(State.BUSY)

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authTask = auth.signInWithCredential(credential)
            authTask.addOnCompleteListener(getOnAuthListener())
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun getOnAuthListener(userName: String? = null) = OnCompleteListener<AuthResult> { finishedTask ->
        if (finishedTask.isSuccessful) {
            _state.postValue(State.IDLE)
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
                        notifyUserChanged()
                    } else {
                        exception = task.exception
                    }
                }
    } 

    private fun notifyUserChanged() {
        _user.value = auth.currentUser
    }


    /*  RESTORE PASSWORD  */

    fun sendRestorePasswordEmail(email: String?) {
        _state.postValue(State.BUSY)

        exception = validator.validateEmail(email)
        if (exception != null) {
            return
        }

        Firebase.auth.sendPasswordResetEmail(email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _restorePasswordEmailSent.value = true
                        _state.postValue(State.IDLE)
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

    /**
     * Returns [ValueEventListener] for user's search history.
     * Clients should not do anything with this listener,
     * only get it here and remove it on disposal in [removeSearchHistoryListener]
     */
    fun getSearchHistoryListener(): ValueEventListener? {
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            _state.postValue(State.BUSY)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val history = mutableListOf<FirebaseSearchRequest>()

                    snapshot.children.reversed().forEach { entrySnap ->
                        entrySnap.getValue<FirebaseSearchRequest>()?.let {
                            history.add(it)
                        }
                    }

                    _searchHistory.value = history
                    _state.postValue(State.IDLE)
                }

                @Suppress("ThrowableNotThrown")
                override fun onCancelled(error: DatabaseError) {
                    exception = error.toException()
                }
            }

            getHistoryReference(currentUser.uid).addValueEventListener(listener)
        } else {
            null
        }
    }

    fun removeSearchHistoryListener(listener: ValueEventListener?) {
        val currentUser = auth.currentUser

        if (currentUser != null && listener != null) {
            getHistoryReference(currentUser.uid).removeEventListener(listener)
        }
    }

    fun updateSearchHistory(request: FirebaseSearchRequest) {
        val currentUser = auth.currentUser ?: return

        _state.postValue(State.BUSY)

        getHistoryReference(currentUser.uid)
                .push()
                .setValue(request)
                .addOnCompleteListener(getOnCompleteListener())
    }

    fun clearSearchHistory() {
        val currentUser = auth.currentUser ?: return

        _state.postValue(State.BUSY)

        getHistoryReference(currentUser.uid).setValue(null)
                .addOnCompleteListener(getOnCompleteListener())
    }

    private fun getOnCompleteListener() = OnCompleteListener<Void> { finishedTask ->
        if (finishedTask.isSuccessful) {
            _state.postValue(State.IDLE)
        } else {
            exception = finishedTask.exception
        }
    }


    /*  RESTORING STATE  */

    fun flushErrorState() {
        if (_state.value == State.ERROR) {
            _state.postValue(State.IDLE)
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