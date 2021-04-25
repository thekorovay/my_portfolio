package com.thekorovay.myportfolio.module_profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentProfileRestorePasswordBinding
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.module_profile.viewmodels.RestorePasswordViewModel
import com.thekorovay.myportfolio.tools.setupNavUpButton
import java.lang.Exception
import org.koin.android.viewmodel.ext.android.viewModel

class RestorePasswordFragment: Fragment() {
    private lateinit var binding: FragmentProfileRestorePasswordBinding

    private val viewModel: RestorePasswordViewModel by viewModel()

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
                viewModel.setErrorMessageDisplayed()
            }
        }

        viewModel.emailSent.observe(viewLifecycleOwner) { emailSent ->
            if (emailSent) {
                showEmailSentMessage()
                viewModel.setSuccessMessageDisplayed()
                goBackToSignIn()
            }
        }

        return binding.root
    }

    private fun showErrorMessage(exception: Exception?) {
        val message = exception?.localizedMessage ?: getString(R.string.unknown_error)

        val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_btn_close) { _, _ -> }
                .create()

        dialog.show()
    }

    private fun showEmailSentMessage() {
        val dialog = AlertDialog.Builder(requireContext())
                .setMessage(R.string.restore_password_email_sent)
                .setPositiveButton(R.string.dialog_btn_close) { _, _ -> }
                .create()

        dialog.show()
    }

    private fun goBackToSignIn() {
        findNavController().navigateUp()
    }
}