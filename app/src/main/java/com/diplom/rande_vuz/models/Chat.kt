package com.diplom.rande_vuz.models

data class Chat(
    val id: String = "",
    val userIds: List<String> = listOf(),
    val lastMessage: String = "",
    val timestamp: Long = 0,
    val chatName: String = "",
    val chatPhotoPath: String? = null
)
