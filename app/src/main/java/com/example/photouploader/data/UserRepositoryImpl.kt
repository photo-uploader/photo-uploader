package com.example.photouploader.data

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.photouploader.ui.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) : UserRepository {

    override val currentUser = MutableLiveData<User?>()

    init {
        firebaseAuth.addAuthStateListener {
            currentUser.value = it.currentUser?.toUser()
        }
    }

    override fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    override fun signInToFbWithGoogle(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            firebaseAuth.signInWithCredential(credential)
        } catch (e: ApiException) {
            Log.wtf(javaClass.simpleName, "Google sign in failed", e)
        }
    }

    override fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }
}