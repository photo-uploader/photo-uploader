package com.example.photouploader.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.photouploader.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : NavigatorFragment(R.layout.fragment_sign_in) {

    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) navigator.navigateTo(Destination.UserProfileScreen)
        }

        view.findViewById<Button>(R.id.b_sign_in).setOnClickListener {
            val destination = Destination.GoogleAccountDialog(
                intent = viewModel.googleSignInIntent,
                onAccountSelected = { viewModel.signInToFbWithGoogle(it) }
            )
            navigator.navigateTo(destination)
        }

    }
}

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser = userRepository.currentUser

    val googleSignInIntent = userRepository.getSignInIntent()

    fun signInToFbWithGoogle(data: Intent) = userRepository.signInToFbWithGoogle(data)

}