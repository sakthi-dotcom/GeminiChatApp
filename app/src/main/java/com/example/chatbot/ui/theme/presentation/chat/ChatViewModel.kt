package com.example.chatbot.ui.theme.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.data.model.ChatMessage
import com.example.chatbot.data.remote.GeminiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val geminiHelper: GeminiHelper
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ChatEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.SendMessage -> sendMessage(intent.message)
            is ChatIntent.ButtonClicked -> handleButtonClick(intent.buttonText)
        }
    }

    private fun sendMessage(message: String) {
        val userMessage = ChatMessage(text = message, isUser = true)
        _state.update { it.copy(messages = it.messages + userMessage, isLoading = true) }

        viewModelScope.launch {
            if (message.trim().equals("hello", ignoreCase = true)) {
                val botMessage = ChatMessage(
                    text = "Welcome to my shop. Are you want coffee?",
                    isUser = false,
                    showButtons = true,
                    buttonsEnabled = true
                )
                _state.update { it.copy(messages = it.messages + botMessage, isLoading = false) }
            } else {
                val reply = geminiHelper.getResponse(message)
                if (reply != null) {
                    val botMessage = ChatMessage(text = reply, isUser = false)
                    _state.update {
                        it.copy(messages = it.messages + botMessage, isLoading = false, error = null)
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to get reply") }
                    _effect.emit(ChatEffect.ShowToast("Something went wrong"))
                }
            }
        }
    }

    private fun handleButtonClick(buttonText: String) {
        viewModelScope.launch {
            // Add the user message for the button click
            val userMessage = ChatMessage(text = buttonText, isUser = true)
            // Disable buttons for the welcome message
            val updatedMessages = _state.value.messages.map { message ->
                if (message.text == "Welcome to my shop. Are you want coffee?" && !message.isUser) {
                    message.copy(buttonsEnabled = false)
                } else {
                    message
                }
            }
            _state.update { it.copy(messages = updatedMessages + userMessage, isLoading = true) }

            if (buttonText == "Yes") {
                val menuMessage = ChatMessage(
                    text = "Here's our coffee menu:\n1. Espresso - $3\n2. Latte - $4\n3. Cappuccino - $4\n4. Americano - $3",
                    isUser = false,
                    showButtons = false
                )
                _state.update { it.copy(messages = it.messages + menuMessage, isLoading = false) }
            } else if (buttonText == "No") {
                val botMessage = ChatMessage(text = "What do you want?", isUser = false)
                _state.update { it.copy(messages = it.messages + botMessage, isLoading = false) }
            }
        }
    }
}