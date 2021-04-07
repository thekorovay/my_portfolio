package com.thekorovay.myportfolio.module_profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentProfileSignInBinding
import com.thekorovay.myportfolio.module_profile.viewmodels.SignInViewModel
import com.thekorovay.myportfolio.tools.setupNavUpButton

class SignInFragment: Fragment() {
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

        binding.run {
            //todo set isLoading according to loading state (not created yet)

            toolbar.setupNavUpButton(findNavController())

            btnSignIn.setOnClickListener {
                viewModel.signIn(etEmail.text.toString(), etPassword.text.toString())
            }
            btnSignInWithGoogle.setOnClickListener {
                viewModel.signInWithGoogle()
            }
        }

        return binding.root
    }
}