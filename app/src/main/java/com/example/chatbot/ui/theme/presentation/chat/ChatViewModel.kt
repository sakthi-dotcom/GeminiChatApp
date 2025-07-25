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
        }
    }

    private fun sendMessage(message: String) {
        val userMessage = ChatMessage(text = message, isUser = true)
        _state.update { it.copy(messages = it.messages + userMessage, isLoading = true) }

        viewModelScope.launch {
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