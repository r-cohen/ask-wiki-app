package com.r.cohen.askwiki.repos.wikipedia

import com.r.cohen.askwiki.repos.wikipedia.models.WikiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiService {
    companion object {
        const val baseUrl = "https://en.wikipedia.org/w/"
    }

    @GET("api.php")
    suspend fun getTitles(
        @Query("action") action: String = "query",
        @Query("prop") prop: String = "extracts",
        @Query("format") format: String = "json",
        @Query("exintro") exintro: String = "",
        @Query("explaintext") explaintext: String = "",
        @Query("redirects") redirects: String = "",
        @Query("titles") titles: String
    ): WikiResponse?

    @GET("api.php")
    suspend fun search(
        @Query("action") action: String = "opensearch",
        @Query("search") search: String,
        @Query("limit") limit: String = "1",
        @Query("format") format: String = "json",
        @Query("namespace") namespace: String = "0",
    ): List<Any>?
}