package com.r.cohen.askwiki.repos.wikipedia

import android.util.Log
import com.google.gson.GsonBuilder
import com.r.cohen.askwiki.repos.wikipedia.models.WikiSearchResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val tag = "wikiapi"

object WikiApi {
    private val apiClient: WikiService = Retrofit.Builder()
        .baseUrl(WikiService.baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(WikiService::class.java)

    //@Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getWikiContent(query: String): String? {
        try {
            val response = apiClient.getTitles(titles = query)
            response?.query?.pages?.keys?.first()?.let { key ->
                return response.query.pages[key]?.extract
            }
        } catch (e: Exception) {
            Log.e(tag, "${e.message}")
        }
        return null
    }

    suspend fun searchWiki(search: String): WikiSearchResult? {
        try {
            apiClient.search(search = search)?.let { result ->
                if (result.size > 3) {
                    val articleNames = result[1] as List<*>
                    if (articleNames.isNotEmpty()) {
                        val name = articleNames[0] as String
                        val linkArray = result[3] as List<*>
                        if (linkArray.isNotEmpty()) {
                            val link = linkArray[0] as String
                            return WikiSearchResult(name, link)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(tag, "${e.message}")
        }
        return null
    }
}