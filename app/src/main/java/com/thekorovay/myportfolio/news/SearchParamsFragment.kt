package com.thekorovay.myportfolio.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentSearchParamsBinding

class SearchParamsFragment: Fragment() {
    private lateinit var binding: FragmentSearchParamsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_params,
            container,
            false
        )

        setupSpinner()

        return binding.root
    }

    private fun setupSpinner() {
        val arrAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.results_per_page_values,
            R.layout.spinner_item
        ).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        binding.spinnerResultsPerPage.apply {
            adapter = arrAdapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    parent?.run {
                        getItemAtPosition(pos) //todo
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { /* ignored */ }
            }
        }
    }
}