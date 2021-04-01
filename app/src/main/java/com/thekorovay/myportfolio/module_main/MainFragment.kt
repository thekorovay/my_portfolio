package com.thekorovay.myportfolio.module_main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        setHasOptionsMenu(true)

        setCards()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.about_app_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun setCards() {
        binding.cardSignInOut.apply {
            //todo set values depending on viewmodel
            tvTitle.text = getString(R.string.card_sign_in)
            ivIcon.setImageResource(R.drawable.ic_sign_in)
            root.setOnClickListener { signIn() }
        }

        binding.cardSearchNews.apply {
            tvTitle.text = getString(R.string.card_news)
            ivIcon.setImageResource(R.drawable.ic_news)
            root.setOnClickListener { searchNews() }
        }

        binding.cardSearchHistory.apply {
            tvTitle.text = getString(R.string.card_search_history)
            ivIcon.setImageResource(R.drawable.ic_search_history)
            root.setOnClickListener { showSearchHistory() }
        }
    }

    private fun signIn() {
        Toast.makeText(requireContext(), "signIn", Toast.LENGTH_SHORT).show()
    }

    private fun signOut() {
        Toast.makeText(requireContext(), "signOut", Toast.LENGTH_SHORT).show()
    }

    private fun searchNews() {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToSearchParamsFragment()
        )
    }

    private fun showSearchHistory() {
        Toast.makeText(requireContext(), "showSearchHistory", Toast.LENGTH_SHORT).show()
    }
}