package com.thekorovay.myportfolio.module_profile.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.MyApplication
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentProfileSignInBinding
import com.thekorovay.myportfolio.network.EasyFirebase
import com.thekorovay.myportfolio.module_profile.viewmodels.SignInViewModel
import com.thekorovay.myportfolio.tools.setupNavUpButton
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import javax.inject.Inject

class SignInFragment: Fragment() {
    private val GOOGLE_SIGN_IN_CODE = 0

    private lateinit var binding: FragmentProfileSignInBinding
    @Inject lateinit var viewModel: SignInViewModel

    override fun onAttach(context: Context) {
        (context.applicationContext as MyApplication).appComponent.inject(this)
        super.onAttach(context)
    }


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

        lifecycleScope.launchWhenStarted {
            viewModel.user.collect { user ->
                user?.let { goBackToProfile() }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.isLoading = state == EasyFirebase.State.BUSY

                if (state == EasyFirebase.State.ERROR) {
                    showErrorMessage(viewModel.exception)
                    viewModel.setErrorMessageDisplayed()
                }
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

        val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_btn_close) { _, _ -> }
                .create()

        dialog.show()
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