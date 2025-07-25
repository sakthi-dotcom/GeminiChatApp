package com.example.chatbot.ui.theme.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.data.model.ChatMessage
import com.example.chatbot.data.remote.GeminiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

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
            is ChatIntent.PlaceOrder -> handlePlaceOrder(intent.items)
            is ChatIntent.UpdateQuantity -> handleUpdateQuantity(intent.messageId, intent.item, intent.quantity)
        }
    }

    private fun sendMessage(message: String) {
        val userMessage = ChatMessage(text = message, isUser = true)
        _state.update { it.copy(messages = it.messages + userMessage, isLoading = true) }

        viewModelScope.launch {
            Log.d("ChatViewModel", "Processing message: $message")
            if (message.trim().equals("hello", ignoreCase = true)) {
                val botMessage = ChatMessage(
                    text = "Welcome to my shop. Do you want the menu?",
                    isUser = false,
                    showButtons = true,
                    buttonsEnabled = true
                )
                _state.update { it.copy(messages = it.messages + botMessage, isLoading = false) }
            } else {
                try {
                    Log.d("ChatViewModel", "Calling Gemini API for message: $message")
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
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "API Error for message '$message': ${e.message}", e)
                    if (e is com.google.ai.client.generativeai.type.ServerException && e.cause?.message?.contains("503") == true) {
                        val botMessage = ChatMessage(
                            text = "Sorry, the system is busy. Please try again later or check back in a few minutes.",
                            isUser = false
                        )
                        _state.update { it.copy(messages = it.messages + botMessage, isLoading = false, error = "Server overloaded") }
                        _effect.emit(ChatEffect.ShowToast("Server is overloaded. Try again later."))
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Unexpected error: ${e.message}") }
                        _effect.emit(ChatEffect.ShowToast("An unexpected error occurred"))
                    }
                }
            }
        }
    }

    private fun handleButtonClick(buttonText: String) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Button clicked: $buttonText")
            val userMessage = ChatMessage(text = buttonText, isUser = true)
            val updatedMessages = _state.value.messages.map { message ->
                if (message.text == "Welcome to my shop. Do you want the menu?" && !message.isUser) {
                    message.copy(buttonsEnabled = false)
                } else {
                    message
                }
            }
            _state.update { it.copy(messages = updatedMessages + userMessage, isLoading = true) }

            if (buttonText == "Yes") {
                val menuItems = mapOf(
                    "Tea" to 0,
                    "Coffee" to 0,
                    "Milk" to 0,
                    "Black Tea" to 0
                )
                val menuMessage = ChatMessage(
                    text = "Please select your items:",
                    isUser = false,
                    buttonsEnabled = true,
                    menuItems = menuItems
                )
                _state.update { it.copy(messages = it.messages + menuMessage, isLoading = false) }
            } else if (buttonText == "No") {
                try {
                    Log.d("ChatViewModel", "Calling Gemini API for 'What do you want?'")
                    val reply = geminiHelper.getResponse("What do you want?")
                    if (reply != null) {
                        val botMessage = ChatMessage(text = reply, isUser = false)
                        _state.update { it.copy(messages = it.messages + botMessage, isLoading = false, error = null) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Failed to get reply") }
                        _effect.emit(ChatEffect.ShowToast("Something went wrong"))
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "API Error for 'What do you want?': ${e.message}", e)
                    if (e is com.google.ai.client.generativeai.type.ServerException && e.cause?.message?.contains("503") == true) {
                        val botMessage = ChatMessage(
                            text = "Sorry, the system is busy. Please try again later or check back in a few minutes.",
                            isUser = false
                        )
                        _state.update { it.copy(messages = it.messages + botMessage, isLoading = false, error = "Server overloaded") }
                        _effect.emit(ChatEffect.ShowToast("Server is overloaded. Try again later."))
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Unexpected error: ${e.message}") }
                        _effect.emit(ChatEffect.ShowToast("An unexpected error occurred"))
                    }
                }
            }
        }
    }

    private fun handlePlaceOrder(items: Map<String, Int>) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Order placed: $items")
            val updatedMessages = _state.value.messages.map { message ->
                if (message.menuItems.isNotEmpty() && !message.isUser) {
                    message.copy(buttonsEnabled = false)
                } else {
                    message
                }
            }
            _state.update { it.copy(messages = updatedMessages, isLoading = true) }

            val orderSummary = items.entries.joinToString("\n- ") { "${it.value}x ${it.key}" }
            val userMessage = ChatMessage(text = "Order:\n- $orderSummary", isUser = true)
            val confirmationMessage = ChatMessage(
                text = "Great choice! Your order of:\n- $orderSummary\nwill be ready soon.",
                isUser = false
            )
            _state.update { it.copy(messages = it.messages + userMessage + confirmationMessage, isLoading = false) }
        }
    }

    private fun handleUpdateQuantity(messageId: Long, item: String, quantity: Int) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Updating quantity for messageId: $messageId, item: $item to $quantity")
            val updatedMessages = _state.value.messages.map { message ->
                if (message.id == messageId && !message.isUser) {
                    Log.d("ChatViewModel", "Found message: ${message.text}, updating $item to $quantity")
                    val newMenuItems = message.menuItems.toMutableMap().apply {
                        this[item] = quantity.coerceAtLeast(1)
                    }
                    message.copy(menuItems = newMenuItems)
                } else {
                    message
                }
            }
            _state.update { it.copy(messages = updatedMessages) }
        }
    }
}