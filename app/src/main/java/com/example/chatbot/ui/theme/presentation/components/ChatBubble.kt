package com.example.chatbot.ui.theme.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.chatbot.data.model.ChatMessage

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) Color.White.copy(alpha = 0.08f) else Color.LightGray.copy(alpha = 0.08f)
    val textColor = if (isUser) Color.White else Color.LightGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .wrapContentWidth(alignment)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            color = bubbleColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}