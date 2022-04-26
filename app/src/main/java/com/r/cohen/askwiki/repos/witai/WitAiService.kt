package com.r.cohen.askwiki.repos.witai

import com.r.cohen.askwiki.repos.witai.models.WitAiResponse
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val apiServiceVersion = "20220410"
private const val defaultContext = "{\"locale\"=\"en_US\",\"timezone\":\"America/Los_Angeles\"}"
private const val apiKey = "GYQIRSGERMILQYCBOKODG5MJBRD4LLBM"

interface WitAiService {
    companion object {
        const val witaiApiUrl = "https://api.wit.ai/"
    }

    @Headers("Authorization: Bearer $apiKey")
    @GET("message")
    suspend fun queryMessage(
        @Query("q") msg: String,
        @Query("verbose") verbose: Boolean = true,
        @Query("context") witaiContext: JSONObject = JSONObject(defaultContext),
        @Query("v") apiVersion: String = apiServiceVersion
    ): WitAiResponse
}