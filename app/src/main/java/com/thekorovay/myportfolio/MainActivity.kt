package com.thekorovay.myportfolio

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/thekorovay/my_portfolio")))
    }

    private fun cry(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}