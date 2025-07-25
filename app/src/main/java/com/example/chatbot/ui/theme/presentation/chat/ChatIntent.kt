package com.example.chatbot.ui.theme.presentation.chat

sealed class ChatIntent {
    data class SendMessage(val message: String) : ChatIntent()
}