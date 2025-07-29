package com.pegasus.fchat.model

sealed class AuthIntent {
    data class EnterEmail(val value: String) : AuthIntent()
    data class EnterPassword(val value: String) : AuthIntent()
    data object Submit : AuthIntent()
    data object Login : AuthIntent()

}
