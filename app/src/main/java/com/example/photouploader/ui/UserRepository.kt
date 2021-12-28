package com.example.photouploader.ui

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.photouploader.data.Item
import com.example.photouploader.data.User

interface UserRepository {

    val currentUser: LiveData<User?>

    fun getSignInIntent(): Intent

    fun signInToFbWithGoogle(data: Intent)

    fun signOut()

    fun uploadItem(item: Item, callback: () -> Unit)
}