package com.thekorovay.myportfolio.module_news.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchReadArticleBinding
import com.thekorovay.myportfolio.tools.setupNavUpButton

class ReadArticleFragment: Fragment() {
    private lateinit var binding: FragmentSearchReadArticleBinding
    private val args: ReadArticleFragmentArgs by navArgs()

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
            R.layout.fragment_search_read_article,
            container,
            false
        )

        binding.toolbar.setupNavUpButton(findNavController())

        binding.article = args.article

        binding.tvSource.setOnClickListener { viewSource() }

        return binding.root
    }

    private fun viewSource() {
        val url = args.article.sourceUrl.toUri()
        startActivity(Intent(Intent.ACTION_VIEW, url))
    }
}