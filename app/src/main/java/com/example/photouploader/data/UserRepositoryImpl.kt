package com.example.photouploader.data

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import com.example.photouploader.ui.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
    private val contentResolver: ContentResolver,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase
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

    override fun uploadItem(item: Item, callback: () -> Unit) {
        val extension = getImageExtension(item.imageUri)
        val fileName = "${System.currentTimeMillis()}.$extension"
        val storageRef = firebaseStorage.reference.child(fileName)
        storageRef.putFile(item.imageUri)
            .addOnSuccessListener { snapshot ->
                snapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { url ->
                        val databaseEntry = DatabaseEntry(url.toString(), item.tag, item.isDefect)
                        addItemToDatabase(databaseEntry, callback)
                    }
            }
            .addOnProgressListener { snapshot ->
                snapshot.bytesTransferred

            }
    }

    private fun addItemToDatabase(databaseEntry: DatabaseEntry, callback: () -> Unit) {
        currentUser.value?.let { user ->
            val databaseRef = firebaseDatabase.getReference("users").child(user.id)
            val id = databaseRef.push().key!!
            databaseRef.child(id).setValue(databaseEntry)
                .addOnCompleteListener {
                    callback.invoke()
                }
        }
    }

    private fun getImageExtension(uri: Uri): String =
        MimeTypeMap.getSingleton().getExtensionFromMimeType(
            contentResolver.getType(uri)
        ) ?: throw IllegalArgumentException("Extension is null")

}