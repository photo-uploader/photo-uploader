package com.example.photouploader.data

import com.google.firebase.auth.FirebaseUser

data class User(
    val name: String,
    val email: String
)

fun FirebaseUser.toUser() = User(
    name = displayName ?: "no name",
    email = email ?: "no email"
)