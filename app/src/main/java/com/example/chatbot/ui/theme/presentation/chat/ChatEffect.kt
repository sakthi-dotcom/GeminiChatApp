package com.example.chatbot.ui.theme.presentation.chat

sealed class ChatEffect {
    data class ShowToast(val message: String) : ChatEffect()
}