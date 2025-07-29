package com.pegasus.fchat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pegasus.fchat.model.ChatIntent
import com.pegasus.fchat.model.ChatMessage
import com.pegasus.fchat.repository.ChatRepository
import com.pegasus.fchat.model.ChatState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        onIntent(ChatIntent.LoadMessages)
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.UpdateMessageText -> {
                _state.update { it.copy(messageText = intent.text) }
            }
            is ChatIntent.SendMessage -> sendMessage(intent.text)
            ChatIntent.LoadMessages -> loadMessages()
        }
    }

    private fun sendMessage(text: String) {
        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            senderId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.sendMessage(newMessage)
            _state.update { it.copy(messageText = "") }
        }
    }

    private fun loadMessages() {
        repository.observeMessages().onEach { messages ->
            _state.update { it.copy(messages = messages) }
        }.launchIn(viewModelScope)
    }
}

