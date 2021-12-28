package com.example.photouploader.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

interface Navigator {
    fun navigateTo(destination: Destination)
    fun pop()
}

sealed class Destination {

    object SignInScreen : Destination()
    object UserProfileScreen: Destination()
    object ImageUploadScreen: Destination()

    data class GoogleAccountDialog(
        val intent: Intent,
        val onAccountSelected: (Intent) -> Unit
    ) : Destination()

}

abstract class NavigatorFragment(layoutRes: Int) : Fragment(layoutRes) {

    lateinit var navigator: Navigator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as? Navigator)?.let { navigator = it }
    }
}