package com.example.praktam_2417051031.data.model

data class ChatMessage(
    val id: String,
    val senderName: String,
    val receiverName: String,
    val content: String,
    val timestamp: String,
    val isFromMe: Boolean
)

data class ChatRoom(
    val contactName: String,
    val lastMessage: String,
    val timestamp: String
)
