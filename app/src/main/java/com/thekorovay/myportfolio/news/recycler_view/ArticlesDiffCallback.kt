package com.thekorovay.myportfolio.news.recycler_view

import androidx.recyclerview.widget.DiffUtil

object ArticlesDiffCallback: DiffUtil.ItemCallback<NewsListItem>() {
    override fun areItemsTheSame(oldItem: NewsListItem, newItem: NewsListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NewsListItem, newItem: NewsListItem): Boolean {
        return oldItem == newItem
    }
}