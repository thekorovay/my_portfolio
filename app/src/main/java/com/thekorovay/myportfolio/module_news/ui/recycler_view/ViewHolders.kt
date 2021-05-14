package com.thekorovay.myportfolio.module_news.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.CardNewsArticleAdapterBinding
import com.thekorovay.myportfolio.databinding.CardNewsArticleAdapterNoThumbnailBinding
import com.thekorovay.myportfolio.entities.UIArticle

class ArticleViewHolder private constructor(
    private val binding: CardNewsArticleAdapterBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): ArticleViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CardNewsArticleAdapterBinding.inflate(inflater, parent, false)
            return ArticleViewHolder(binding)
        }
    }

    fun bind(
        newsArticle: UIArticle,
        newsItemClickListener: NewsItemClickListener
    ) {
        binding.article = newsArticle
        binding.clickListener = newsItemClickListener
    }
}

class ArticleViewHolderNoThumbnail private constructor(
    private val binding: CardNewsArticleAdapterNoThumbnailBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): ArticleViewHolderNoThumbnail {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CardNewsArticleAdapterNoThumbnailBinding
                .inflate(inflater, parent, false)
            return ArticleViewHolderNoThumbnail(binding)
        }
    }

    fun bind(
        newsArticle: UIArticle,
        newsItemClickListener: NewsItemClickListener
    ) {
        binding.article = newsArticle
        binding.clickListener = newsItemClickListener
    }
}

class ShowMoreViewHolder private constructor(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun from(parent: ViewGroup): ShowMoreViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.show_more_news_adapter, parent, false)
            return ShowMoreViewHolder(view)
        }
    }

    fun bind(clickListener: ShowMoreClickListener) {
        itemView.setOnClickListener { clickListener.listener() }
    }
}