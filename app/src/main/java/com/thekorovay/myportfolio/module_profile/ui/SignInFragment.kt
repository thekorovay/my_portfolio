package com.thekorovay.myportfolio.module_profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentProfileSignInBinding
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.module_profile.viewmodels.SignInViewModel
import com.thekorovay.myportfolio.tools.setupNavUpButton
import java.lang.Exception

class SignInFragment: Fragment() {
    private val GOOGLE_SIGN_IN_CODE = 0

    private lateinit var binding: FragmentProfileSignInBinding

    private val viewModel: SignInViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_sign_in,
            container,
            false
        )

        binding.toolbar.setupNavUpButton(findNavController())

        binding.viewModel = viewModel

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let { goBackToProfile() }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.isLoading = state == EasyFirebase.State.BUSY

            if (state == EasyFirebase.State.ERROR) {
                showErrorMessage(viewModel.exception)
                viewModel.setErrorMessageDisplayed()
            }
        }

        viewModel.navigateToRestorePassword.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navigateToRestorePassword()
            }
        }

        binding.btnSignInWithGoogle.setOnClickListener { signInWithGoogle() }

        return binding.root
    }

    private fun signInWithGoogle() {
        val googleSignInIntent = viewModel.getGoogleSignInIntent(requireActivity())
        startActivityForResult(googleSignInIntent, GOOGLE_SIGN_IN_CODE)
    }

    private fun showErrorMessage(exception: Exception?) {
        val message = exception?.localizedMessage ?: getString(R.string.unknown_error)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(R.string.hide) { /* Just hide the snackbar */ }
            .show()
    }

    private fun goBackToProfile() {
        findNavController().navigateUp()
    }

    private fun navigateToRestorePassword() {
        findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToRestorePasswordFragment(
                        binding.etEmail.text.toString()
                )
        )
        viewModel.setNavigationCompleted()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            viewModel.signInWithGoogle(data)
        }
    }
}