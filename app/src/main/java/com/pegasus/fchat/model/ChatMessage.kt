package com.pegasus.fchat.model

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
