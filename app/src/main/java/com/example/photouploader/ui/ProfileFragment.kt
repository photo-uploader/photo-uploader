package com.example.photouploader.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.photouploader.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : NavigatorFragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()

    lateinit var tvUserName: TextView
    lateinit var tvUserEmail: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUserName = view.findViewById(R.id.tv_user_name)
        tvUserEmail = view.findViewById(R.id.tv_user_email)

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                tvUserName.text = user.name
                tvUserEmail.text = user.email
            } else {
                navigator.pop()
            }
        }

        view.findViewById<Button>(R.id.b_sign_out).setOnClickListener {
            viewModel.signOut()
        }

    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    val currentUser = repository.currentUser

    fun signOut() = repository.signOut()

}