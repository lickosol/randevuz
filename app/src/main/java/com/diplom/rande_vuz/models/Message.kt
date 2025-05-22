package com.diplom.rande_vuz.models

data class Message(
    val content: String = "",
    val timestamp: Long = 0L,
    val senderId: String = "",
    val read: Boolean = false
)
