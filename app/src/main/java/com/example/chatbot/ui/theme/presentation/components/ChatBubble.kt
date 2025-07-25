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
import android.util.Log

@Composable
fun ChatBubble(
    message: ChatMessage,
    onButtonClick: ((String) -> Unit)? = null,
    onPlaceOrder: ((Map<String, Int>) -> Unit)? = null,
    onQuantityChange: ((Long, String, Int) -> Unit)? = null
) {
    val bubbleColor = if (message.isUser) Color(0xFFDEDEE3) else Color(0xFF1E90FF)
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp)
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
                    .widthIn(max = 300.dp)
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
                        onClick = {
                            Log.d("ChatBubble", "Yes button clicked")
                            onButtonClick?.invoke("Yes")
                        },
                        enabled = message.buttonsEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDEDEE3),
                            disabledContainerColor = Color(0xFFDEDEE3).copy(alpha = 0.5f)
                        )
                    ) {
                        Text("Yes", color = Color.Black)
                    }
                    Button(
                        onClick = {
                            Log.d("ChatBubble", "No button clicked")
                            onButtonClick?.invoke("No")
                        },
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
            if (message.menuItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 12.dp)
                        .background(color = bubbleColor.copy(alpha = 0.9f), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    message.menuItems.forEach { (item, quantity) ->
                        Row(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        Log.d("ChatBubble", "Minus button clicked for $item, messageId: ${message.id}, quantity: $quantity, enabled: ${message.buttonsEnabled && quantity > 1}")
                                        onQuantityChange?.invoke(message.id, item, quantity - 1)
                                    },
                                    enabled = message.buttonsEnabled && quantity > 1,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF888888),
                                        disabledContainerColor = Color(0xFF888888).copy(alpha = 0.5f),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 6.dp)
                                ) {
                                    Text("−", color = Color.White, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                                }
                                Text(
                                    text = quantity.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                Button(
                                    onClick = {
                                        Log.d("ChatBubble", "Plus button clicked for $item, messageId: ${message.id}, quantity: $quantity, enabled: ${message.buttonsEnabled}")
                                        onQuantityChange?.invoke(message.id, item, quantity + 1)
                                    },
                                    enabled = message.buttonsEnabled,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF888888),
                                        disabledContainerColor = Color(0xFF888888).copy(alpha = 0.5f),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(start = 6.dp)
                                ) {
                                    Text("+", color = Color.White, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                                }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            Log.d("ChatBubble", "Order button clicked, items: ${message.menuItems}")
                            onPlaceOrder?.invoke(message.menuItems)
                        },
                        enabled = message.buttonsEnabled && message.menuItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDEDEE3),
                            disabledContainerColor = Color(0xFFDEDEE3).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Place Order", color = Color.Black)
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