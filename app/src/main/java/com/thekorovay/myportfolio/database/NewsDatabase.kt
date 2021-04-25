package com.thekorovay.myportfolio.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Dao
interface ArticlesDao {
    @Query("SELECT * FROM articles")
    fun getArticles(): LiveData<List<DatabaseArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg articles: DatabaseArticle)

    @Query("DELETE FROM articles")
    suspend fun clearAll()
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY date_time DESC LIMIT 1")
    fun getLastRequest(): LiveData<DatabaseSearchRequest?>

    @Query("SELECT * FROM search_history ORDER BY date_time DESC")
    fun getHistory(): LiveData<List<DatabaseSearchRequest>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(searchRequest: DatabaseSearchRequest)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()
}


@Database(entities = [DatabaseArticle::class, DatabaseSearchRequest::class], version = 2)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articlesDao(): ArticlesDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}


private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE search_history (
                date_time TEXT PRIMARY KEY NOT NULL,
                query TEXT NOT NULL,
                safe_search_enabled INTEGER NOT NULL,
                thumbs_enabled INTEGER NOT NULL, 
                page_size INTEGER NOT NULL
            )
        """.trimIndent())
    }
}

fun getNewsDatabase(context: Context) = Room
    .databaseBuilder(context, NewsDatabase::class.java, "news_db")
    .addMigrations(MIGRATION_1_2)
    .build()