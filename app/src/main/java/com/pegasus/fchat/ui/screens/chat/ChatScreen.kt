package com.pegasus.fchat.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.pegasus.fchat.R
import com.pegasus.fchat.ui.components.ChatHeader
import com.pegasus.fchat.model.ChatIntent
import com.pegasus.fchat.model.ChatState
import com.pegasus.fchat.utils.TimeUtil.formatTimestamp

@Composable
fun ChatScreen(
    state: ChatState,
    onIntent: (ChatIntent) -> Unit
) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding()) {
            ChatHeader(isOnline = true)
            HorizontalDivider(thickness = 1.dp, color = Color.Black)
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.chat_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp),
                    reverseLayout = true
                ) {
                    items(state.messages.reversed()) { msg ->
                        val isCurrentUser =
                            msg.senderId == FirebaseAuth.getInstance().currentUser?.uid
                        val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
                        val backgroundColor =
                            if (isCurrentUser) Color(0xFFDCF8C6) else Color(
                                0xFFEDEDED
                            )
                        val bubbleShape = if (isCurrentUser) {
                            RoundedCornerShape(12.dp, 0.dp, 12.dp, 12.dp)
                        } else {
                            RoundedCornerShape(0.dp, 12.dp, 12.dp, 12.dp)
                        }
                        Column(
                            horizontalAlignment = alignment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(backgroundColor, bubbleShape)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(text = msg.text)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatTimestamp(msg.timestamp),
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider()

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.messageText,
                    onValueChange = { onIntent(ChatIntent.UpdateMessageText(it)) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E88E5),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF1E88E5),
                        focusedLabelColor = Color(0xFF1E88E5)
                    ),
                )
                IconButton(
                    enabled = state.messageText.isNotEmpty(),
                    onClick = {
                        onIntent(ChatIntent.SendMessage(state.messageText))
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview(){

}