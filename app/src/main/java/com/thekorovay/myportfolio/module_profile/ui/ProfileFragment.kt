package com.thekorovay.myportfolio.module_profile.ui

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentProfileBinding
import com.thekorovay.myportfolio.module_profile.viewmodels.ProfileViewModel

class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()


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

        binding.btnSignIn.setOnClickListener { viewModel.swap() }

        viewModel.isSignedIn.observe(viewLifecycleOwner) { signedIn ->
            binding.isSignedIn = signedIn
            animateIcon(signedIn)
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
}