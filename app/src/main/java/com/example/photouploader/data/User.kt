package com.example.photouploader.data

import com.google.firebase.auth.FirebaseUser

data class User(
    val id: String,
    val name: String,
    val email: String
)

fun FirebaseUser.toUser() = User(
    id = uid,
    name = displayName ?: "no name",
    email = email ?: "no email"
)