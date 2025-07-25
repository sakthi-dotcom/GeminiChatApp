package com.example.chatbot.data.remote

import android.util.Log
import com.example.chatbot.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject

class GeminiHelper @Inject constructor() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun getResponse(prompt: String): String? {
        return try {
            val response = generativeModel.generateContent(prompt)
            Log.d("GeminiResponse", response.text ?: "No content")
            response.text
        } catch (e: Exception) {
            Log.e("GeminiHelper", "Error: ${e.message}", e)
            null
        }
    }
}