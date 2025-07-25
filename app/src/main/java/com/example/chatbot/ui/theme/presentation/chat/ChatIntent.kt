package com.example.chatbot.ui.theme.presentation.chat

sealed class ChatIntent {
    data class SendMessage(val message: String) : ChatIntent()
    data class ButtonClicked(val buttonText: String) : ChatIntent()
    data class PlaceOrder(val items: Map<String, Int>) : ChatIntent()
    data class UpdateQuantity(val messageId: Long, val item: String, val quantity: Int) : ChatIntent()
}