package com.r.cohen.askwiki.repos.wikipedia

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val tag = "wikiapi"

object WikiApi {
    private val apiClient: WikiService = Retrofit.Builder()
        .baseUrl(WikiService.baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(WikiService::class.java)

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getWikiContent(query: String): String? {
        try {
            val response = apiClient.getTitles(titles = query)
            response.query?.pages?.keys?.first()?.let { key ->
                return response.query.pages[key]?.extract
            }
        } catch (e: Exception) {
            Log.e(tag, "${e.message}")
        }
        return null
    }
}