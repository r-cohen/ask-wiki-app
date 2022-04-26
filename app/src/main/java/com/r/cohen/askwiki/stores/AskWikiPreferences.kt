package com.r.cohen.askwiki.stores

import android.content.Context

private const val sharedPrefsName = "AskWikiPreferences"
private const val keyIsFirstTimeToolTipShown = "isFirstTimeToolTipShown"

class AskWikiPreferences(context: Context) {
    private val sharedPref = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)

    var isFirstTimeToolTipShown: Boolean
        get() = sharedPref.getBoolean(keyIsFirstTimeToolTipShown, false)
        set(value) = sharedPref.edit().putBoolean(keyIsFirstTimeToolTipShown, value).apply()
}