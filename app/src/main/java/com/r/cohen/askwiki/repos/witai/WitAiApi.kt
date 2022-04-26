package com.r.cohen.askwiki.repos.witai

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val tag = "witapi"

object WitAiApi {
    private val apiClient: WitAiService = Retrofit.Builder()
        .baseUrl(WitAiService.witaiApiUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(WitAiService::class.java)

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun parseQuestionForEntity(question: String): String? {
        try {
            val response = apiClient.queryMessage(msg = question)
            response.entities?.wikipedia_search_query?.let { entities ->
                if (entities.isNotEmpty()) {
                    return entities.last().value
                }
            }
            Log.d(tag, "response: $response")
        } catch (e: Exception) {
            Log.e(tag, "${e.message}")
        }
        return null
    }
}