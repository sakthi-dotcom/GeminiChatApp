package com.example.chatbot.ui.theme.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chatbot.data.model.ChatMessage
import com.example.chatbot.ui.theme.presentation.components.ChatBubble
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatState,
    onIntent: (ChatIntent) -> Unit,
    effectFlow: Flow<ChatEffect>
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        effectFlow.collectLatest { effect ->
            if (effect is ChatEffect.ShowToast) {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CoffeeBot",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.SansSerif)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF5E85AF))
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
            ) {
                ChatInputBar(
                    onSend = { onIntent(ChatIntent.SendMessage(it)) },
                    isLoading = state.isLoading
                )
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .blur(50.dp)
            )

            if (state.messages.isEmpty()) {
                Text(
                    "Say Hello to CoffeeBot ☕",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    reverseLayout = true
                ) {
                    items(state.messages.reversed()) { message ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                        ) {
                            ChatBubble(message = message)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}