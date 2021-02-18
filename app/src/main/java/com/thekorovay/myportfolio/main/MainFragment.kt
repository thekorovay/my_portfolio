package com.thekorovay.myportfolio.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentMainBinding

class MainFragment: Fragment() {

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
        binding.cardPlay.apply {
            tvTitle.text = getString(R.string.card_play_game)
            ivIcon.setImageResource(R.drawable.ic_play)
            root.setOnClickListener { playGame() }
        }
        binding.cardLogin.apply {
            tvTitle.text = getString(R.string.card_login)
            ivIcon.setImageResource(R.drawable.ic_login)
            root.setOnClickListener { login() }
        }
        binding.cardNews.apply {
            tvTitle.text = getString(R.string.card_news)
            ivIcon.setImageResource(R.drawable.ic_news)
            root.setOnClickListener { readNews() }
        }
        binding.cardMagic8Ball.apply {
            tvTitle.text = getString(R.string.card_know_future)
            ivIcon.setImageResource(R.drawable.ic_magic_8_ball)
            root.setOnClickListener { knowFuture() }
        }
        binding.cardSourceCode.apply {
            tvTitle.text = getString(R.string.card_src_code)
            ivIcon.setImageResource(R.drawable.ic_source_code)
            root.setOnClickListener { checkSourceCode() }
        }
    }

    private fun playGame() {
        cry("PLAY")
    }

    private fun login() {
        cry("LOGIN")
    }

    private fun readNews() {
        cry("NEWS")
    }

    private fun knowFuture() {
        cry("FUTURE")
    }

    private fun checkSourceCode() {
        // todo move link to the firebase or to the string resources later
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/thekorovay/my_portfolio")))
    }

    private fun cry(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}