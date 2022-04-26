package com.r.cohen.askwiki.repos.witai.models

data class WitAiResponse(
    val text: String,
    val intents: List<WitAiIntent>,
    val entities: WitAiEntity?
)
