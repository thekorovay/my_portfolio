package com.thekorovay.myportfolio.news.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchReadArticleBinding

class ReadArticleFragment: Fragment() {
    private lateinit var binding: FragmentSearchReadArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_read_article,
            container,
            false
        )

        viewArticleData()

        binding.tvSource.setOnClickListener { viewSource() }

        return binding.root
    }

    private fun viewArticleData() {
        //todo
    }

    private fun viewSource() {
        //todo startActivity(Intent(Intent.ACTION_VIEW, stringUri.toUri()))
    }
}