package com.example.photouploader

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.photouploader.ui.SignInFragment
import com.example.photouploader.ui.ProfileFragment
import com.example.photouploader.ui.Destination
import com.example.photouploader.ui.Navigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    private var onAccountSelected: ((Intent) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            navigateToFragment(SignInFragment(), addToBack = false)
        }
    }

    override fun navigateTo(destination: Destination) = when (destination) {
        Destination.SignInScreen -> navigateToFragment(SignInFragment())
        Destination.UserProfileScreen -> navigateToFragment(ProfileFragment())
        is Destination.GoogleAccountDialog -> showGoogleAccountDialog(destination)
    }

    private fun navigateToFragment(fragment: Fragment, addToBack: Boolean = true) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            if (addToBack) addToBackStack(fragment.javaClass.simpleName)
        }
    }

    private fun showGoogleAccountDialog(destination: Destination.GoogleAccountDialog) {
        onAccountSelected = destination.onAccountSelected
        startActivityForResult(destination.intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK && data != null) {
            onAccountSelected?.invoke(data)
        }
    }

    override fun pop() {
        supportFragmentManager.popBackStack()
    }

    companion object {
        const val RC_SIGN_IN = 111
    }

}