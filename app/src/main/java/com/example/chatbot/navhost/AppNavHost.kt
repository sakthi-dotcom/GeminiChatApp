package com.example.chatbot.navhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatbot.ui.screens.SplashScreen
import com.example.chatbot.ui.theme.presentation.chat.ChatScreen
import com.example.chatbot.ui.theme.presentation.chat.ChatViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
){

    NavHost(navController = navController, startDestination = "splash"){
        composable("splash") {
            SplashScreen {
                navController.navigate("chat") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("chat") {
            val viewModel: ChatViewModel = hiltViewModel()
            ChatScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = { viewModel.onIntent(it) },
                effectFlow = viewModel.effect
            )
        }

    }
}