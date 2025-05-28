package com.diplom.rande_vuz.models

data class MessageDisplay(
    val content: String,
    val senderName: String,
    val timestamp: Long,
    val read: Boolean,
    val senderId: String
)