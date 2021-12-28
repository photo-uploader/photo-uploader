package com.example.photouploader.data

import android.net.Uri

data class Item(
    val imageUri: Uri,
    val tag: String,
    val isDefect: Boolean
)