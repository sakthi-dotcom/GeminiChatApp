package com.example.chatbot.ui.theme.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.chatbot.ui.theme.presentation.components.TypingDotsLoader
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
                        "Coffee-Bot",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Monospace)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFDEDEE3))
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
                    "Say Hello to Coffee-Bot ☕",
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
                    if (state.isLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFDEDEE3),
                                            shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    TypingDotsLoader()
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

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