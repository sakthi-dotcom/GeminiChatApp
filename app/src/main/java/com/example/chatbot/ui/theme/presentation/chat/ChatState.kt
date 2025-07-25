package com.example.chatbot.ui.theme.presentation.chat

import com.example.chatbot.data.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)