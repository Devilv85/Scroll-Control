package com.vishal.thinkpadcontrol.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class QuoteRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var quotes: List<String> = emptyList()

    init {
        loadQuotes()
    }

    private fun loadQuotes() {
        try {
            val json = context.assets.open("motivational_quotes.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<String>>() {}.type
            quotes = Gson().fromJson(json, type)
        } catch (e: Exception) {
            quotes = getDefaultQuotes()
        }
    }

    fun getRandomQuote(): String {
        return if (quotes.isNotEmpty()) {
            quotes[Random.nextInt(quotes.size)]
        } else {
            "Take a moment to reflect on your goals."
        }
    }

    private fun getDefaultQuotes(): List<String> {
        return listOf(
            "Focus on what matters most to you right now.",
            "Every moment of distraction is a moment away from your dreams.",
            "Your future self will thank you for this decision.",
            "Progress happens when you choose intention over impulse.",
            "What would you accomplish if you weren't scrolling?",
            "Your attention is your most valuable resource.",
            "Small choices lead to big changes.",
            "Be present in your own life.",
            "Your goals are waiting for your focus.",
            "Choose growth over entertainment."
        )
    }
}