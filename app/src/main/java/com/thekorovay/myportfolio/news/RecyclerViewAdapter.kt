package com.thekorovay.myportfolio.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thekorovay.myportfolio.databinding.CardNewsArticleAdapterBinding
import com.thekorovay.myportfolio.news.model.Article

class RecyclerViewAdapter: ListAdapter<Article, RecyclerViewAdapter.NewsViewHolder>(ArticlesDiffCallback) {

    object ArticlesDiffCallback: DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NewsViewHolder.from(parent)

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NewsViewHolder private constructor(val binding: CardNewsArticleAdapterBinding): RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): NewsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = CardNewsArticleAdapterBinding.inflate(inflater, parent, false)
                return NewsViewHolder(binding)
            }
        }

        fun bind(article: Article) {
            binding.article = article
        }
    }
}