package com.example.chatbot.ui.theme.presentation.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatInputBar(
    onSend: (String) -> Unit,
    isLoading: Boolean
) {
    var input by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Ask CoffeeBot...") },
            singleLine = true
        )

        IconButton(
            onClick = {
                if (input.isNotBlank()) {
                    onSend(input.trim())
                    input = ""
                }
            },
            enabled = !isLoading
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }
}
