package com.example.chatbot.ui.theme.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.chatbot.data.model.ChatMessage
import com.example.chatbot.ui.theme.presentation.chat.ChatIntent
import kotlinx.coroutines.delay

@Composable
fun ChatBubble(
    message: ChatMessage,
    onButtonClick: ((String) -> Unit)? = null
) {
    val bubbleColor = if (message.isUser) Color(0xFFDEDEE3) else Color(0xFFDEDEE3)
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Column {
            Box(
                modifier = Modifier
                    .background(color = bubbleColor, shape = shape)
                    .padding(12.dp)
                    .widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (message.showButtons) {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onButtonClick?.invoke("Yes") },
                        enabled = message.buttonsEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDEDEE3),
                            disabledContainerColor = Color(0xFFDEDEE3).copy(alpha = 0.5f)
                        )
                    ) {
                        Text("Yes", color = Color.Black)
                    }
                    Button(
                        onClick = { onButtonClick?.invoke("No") },
                        enabled = message.buttonsEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDEDEE3),
                            disabledContainerColor = Color(0xFFDEDEE3).copy(alpha = 0.5f)
                        )
                    ) {
                        Text("No", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun TypingDotsLoader() {
    val dotCount = 3
    val delays = listOf(0, 150, 300)

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(dotCount) { index ->
            val scale = rememberInfiniteTransition().animateFloat(
                initialValue = 0.7f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500, delayMillis = delays[index], easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale.value)
                    .background(Color.Black, shape = CircleShape)
            )
        }
    }
}