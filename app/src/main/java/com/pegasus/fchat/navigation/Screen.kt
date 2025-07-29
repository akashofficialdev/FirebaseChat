package com.pegasus.fchat.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login: Screen("login")
    object Chat: Screen("chat")
}