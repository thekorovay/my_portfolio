package com.thekorovay.myportfolio.module_profile.ui

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.MyApplication
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentProfileBinding
import com.thekorovay.myportfolio.domain.entities.ProfileState
import com.thekorovay.myportfolio.module_profile.viewmodels.ProfileViewModel
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import javax.inject.Inject

class ProfileFragment: Fragment() {

    private lateinit var binding: FragmentProfileBinding
    @Inject lateinit var viewModel: ProfileViewModel

    override fun onAttach(context: Context) {
        (context.applicationContext as MyApplication).appComponent.inject(this)
        super.onAttach(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )

        binding.viewModel = viewModel

        lifecycleScope.launchWhenStarted {
            viewModel.user.collect { user ->
                binding.user = user
                animateIcon(user != null)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                binding.isLoading = state == ProfileState.BUSY

                if (state == ProfileState.ERROR) {
                    showErrorMessage(viewModel.exception)
                    viewModel.setErrorMessageDisplayed()
                }
            }
        }

        viewModel.navigatingTo.observe(viewLifecycleOwner) { direction ->
            when (direction) {
                ProfileViewModel.Directions.SIGN_IN -> {
                    navigateTo(ProfileFragmentDirections.actionProfileFragmentToSignInFragment())
                }
                ProfileViewModel.Directions.SIGN_UP -> {
                    navigateTo(ProfileFragmentDirections.actionProfileFragmentToSignUpFragment())
                }
                null -> { /* No navigation required */ }
            }
        }

        return binding.root
    }

    private fun animateIcon(isSignedIn: Boolean) {
        animateIconDrawable(isSignedIn)
        spinIcon()
    }

    private fun animateIconDrawable(isSignedIn: Boolean) {
        binding.ivStatus.setImageDrawable(ResourcesCompat.getDrawable(
            resources,
            if (isSignedIn) R.drawable.power_off_to_on else R.drawable.power_on_to_off,
            null
        ))

        val iconAnimation = binding.ivStatus.drawable as AnimationDrawable
        iconAnimation.apply {
            setExitFadeDuration(1000)
            setEnterFadeDuration(1000)
        }

        iconAnimation.start()
    }

    private fun spinIcon() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.spin_clockwise)
        binding.ivStatus.startAnimation(animation)
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

    private fun navigateTo(direction: NavDirections) {
        findNavController().navigate(direction)
        viewModel.setNavigationCompleted()
    }
}