package com.pegasus.fchat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pegasus.fchat.ui.screens.login.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pegasus.fchat.ui.screens.chat.ChatScreen
import com.pegasus.fchat.ui.screens.chat.ChatViewModel
import com.pegasus.fchat.ui.screens.login.LoginScreen
import com.pegasus.fchat.ui.screens.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                state = viewModel.state.value,
                onIntent = viewModel::onIntent,
                effect = viewModel.effect,
                onLoginSuccess = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Chat.route) {
            val viewModel: ChatViewModel = hiltViewModel()
            ChatScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = viewModel::onIntent
            )
        }
    }
}

