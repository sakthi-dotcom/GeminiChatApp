package com.example.chatbot.data.model

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean,
    val showButtons: Boolean = false,
    val buttonsEnabled: Boolean = true
)