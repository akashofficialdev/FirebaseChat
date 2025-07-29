package com.pegasus.fchat.model

sealed class ChatIntent {
    data class UpdateMessageText(val text: String) : ChatIntent()
    data class SendMessage(val text: String) : ChatIntent()
    data object LoadMessages : ChatIntent()
}
