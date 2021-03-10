package com.thekorovay.myportfolio.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchArticlesListBinding

class SearchResultsFragment: Fragment() {
    private lateinit var binding: FragmentSearchArticlesListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_articles_list,
            container,
            false
        )

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
         //todo
    }
}