package com.thekorovay.myportfolio.module_news.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class RecyclerViewAdapter(
    private val newsItemClickListener: NewsItemClickListener,
    private val showMoreClickListener: ShowMoreClickListener,
    ) : ListAdapter<NewsListItem, RecyclerView.ViewHolder>(ArticlesDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            NewsListItem.ARTICLE -> ArticleViewHolder.from(parent)
            NewsListItem.ARTICLE_NO_THUMBNAIL -> ArticleViewHolderNoThumbnail.from(parent)
            NewsListItem.SHOW_MORE_BUTTON -> ShowMoreViewHolder.from(parent)
            else -> throw Exception("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ArticleViewHolder -> {
                val articleItem = getItem(position) as NewsListItem.ArticleItem
                holder.bind(articleItem.article, newsItemClickListener)
            }
            is ArticleViewHolderNoThumbnail -> {
                val articleItem = getItem(position) as NewsListItem.ArticleNoThumbnailItem
                holder.bind(articleItem.article, newsItemClickListener)
            }
            is ShowMoreViewHolder -> {
                holder.bind(showMoreClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).type
}