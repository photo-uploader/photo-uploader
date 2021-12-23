package com.example.photouploader.ui

import android.content.Intent
import androidx.lifecycle.LiveData
import com.example.photouploader.data.User

interface UserRepository {

    val currentUser: LiveData<User?>

    fun getSignInIntent(): Intent

    fun signInToFbWithGoogle(data: Intent)

    fun signOut()

}