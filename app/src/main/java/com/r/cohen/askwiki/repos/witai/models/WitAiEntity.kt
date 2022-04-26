package com.r.cohen.askwiki.repos.witai.models

import com.google.gson.annotations.SerializedName

data class WitAiEntity(
    @SerializedName("wit\$wikipedia_search_query:wikipedia_search_query")
    val wikipedia_search_query: List<WitAiEntityContent>?
)
