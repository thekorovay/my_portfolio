package com.thekorovay.myportfolio.module_profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentProfileRestorePasswordBinding
import com.thekorovay.myportfolio.firebase.EasyFirebase
import com.thekorovay.myportfolio.module_profile.viewmodels.RestorePasswordViewModel
import com.thekorovay.myportfolio.tools.setupNavUpButton
import java.lang.Exception

class RestorePasswordFragment: Fragment() {
    private lateinit var binding: FragmentProfileRestorePasswordBinding

    private val viewModel: RestorePasswordViewModel by viewModels()

    private val args: RestorePasswordFragmentArgs by navArgs()


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
            R.layout.fragment_profile_restore_password,
            container,
            false
        )

        binding.toolbar.setupNavUpButton(findNavController())

        binding.viewModel = viewModel

        viewModel.email.value = args.email

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.isLoading = state == EasyFirebase.State.BUSY

            if (state == EasyFirebase.State.ERROR) {
                showErrorMessage(viewModel.exception)
            }
        }

        viewModel.emailSent.observe(viewLifecycleOwner) { emailSent ->
            if (emailSent) {
                showEmailSentMessage()
            }
        }

        return binding.root
    }

    private fun showErrorMessage(exception: Exception?) {
        val message = exception?.localizedMessage ?: getString(R.string.unknown_error)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(R.string.hide) { /* Just hide the snackbar */ }
            .show()

        viewModel.setErrorMessageDisplayed()
    }

    private fun showEmailSentMessage() {
        Snackbar.make(binding.root, getString(R.string.restore_password_email_sent), Snackbar.LENGTH_LONG)
                .setAction(R.string.hide) { /* Just hide the snackbar */ }
                .show()

        viewModel.setSuccessMessageDisplayed()

        goBackToSignIn()
    }

    private fun goBackToSignIn() {
        findNavController().navigateUp()
    }
}