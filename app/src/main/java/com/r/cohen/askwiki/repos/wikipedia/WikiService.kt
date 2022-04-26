package com.r.cohen.askwiki.repos.wikipedia

import com.r.cohen.askwiki.repos.wikipedia.models.WikiResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val apiAction = "query"
private const val apiProp = "extracts"
private const val apiFormat = "json"
private const val apiExintro = ""

interface WikiService {
    companion object {
        const val baseUrl = "https://en.wikipedia.org/w/"
    }

    @GET("api.php")
    suspend fun getTitles(
        @Query("action") action: String = apiAction,
        @Query("prop") prop: String = apiProp,
        @Query("format") format: String = apiFormat,
        @Query("exintro") exintro: String = apiExintro,
        @Query("titles") titles: String
    ): WikiResponse
}