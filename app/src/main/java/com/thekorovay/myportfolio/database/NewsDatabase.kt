package com.thekorovay.myportfolio.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

const val DATABASE_NAME = "news_db"
const val ARTICLES_TABLE_NAME = "articles"

@Dao
interface NewsDao {
    @Query("SELECT * FROM $ARTICLES_TABLE_NAME")
    fun getArticles(): LiveData<List<DatabaseArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg articles: DatabaseArticle)

    @Query("DELETE FROM $ARTICLES_TABLE_NAME")
    fun clearAll()
}

@Database(entities = [DatabaseArticle::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}


private lateinit var DATABASE_INSTANCE: NewsDatabase

fun getNewsDatabase(context: Context): NewsDatabase {
    if (!::DATABASE_INSTANCE.isInitialized) {
        DATABASE_INSTANCE = Room.databaseBuilder(context, NewsDatabase::class.java, DATABASE_NAME)
            .build()
    }

    return DATABASE_INSTANCE
}