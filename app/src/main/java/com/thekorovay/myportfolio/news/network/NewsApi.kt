package com.thekorovay.myportfolio.news.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val FIRST_PAGE_NUMBER = 1

const val BASE_URL = "https://contextualwebsearch-websearch-v1.p.rapidapi.com/"
const val HEADER_KEY = "x-rapidapi-key: 751179a5c0msh4eb80c4abc5b438p10850cjsn0148d7a42070"
const val HEADER_HOST = "x-rapidapi-host: contextualwebsearch-websearch-v1.p.rapidapi.com"
const val STATIC_QUERY_PART = "fromPublishedDate=null&toPublishedDate=null&autoCorrect=false&withThumbnails=true"
const val PATH = "api/search/NewsSearchAPI"

interface INewsService {
    @Headers(HEADER_KEY, HEADER_HOST)
    @GET("$PATH?$STATIC_QUERY_PART")
    suspend fun requestNewsArticlesAsync(
        @Query("q") query: String,
        @Query("safeSearch") safeSearchEnabled: Boolean,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int
    ): NewsServerResponse
}


val newsApi: INewsService by lazy {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    retrofit.create(INewsService::class.java)
}