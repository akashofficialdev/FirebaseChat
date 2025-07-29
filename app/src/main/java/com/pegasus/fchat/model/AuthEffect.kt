package com.pegasus.fchat.model

sealed class AuthEffect {
    object NavigateToChat : AuthEffect()
    data class ShowToast(val message: String) : AuthEffect()
}
