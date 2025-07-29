package com.pegasus.fchat.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pegasus.fchat.model.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val chatId = "newChat"

    fun observeMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val messages = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(message: ChatMessage) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(message)
            .await()
    }
}
