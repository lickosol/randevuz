package com.diplom.rande_vuz.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Chat(
    val chatId: String = "",
    val lastMessage: String = "",
    val otherUserName: String = ""
)

class ChatListViewModel : ViewModel() {
    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> get() = _chatList

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loadChats() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                val chats = result.map { doc ->
                    val participants = doc.get("participants") as? List<*>
                    val otherUserName = (participants?.firstOrNull { it != currentUserId })?.toString() ?: "Unknown"
                    Chat(
                        chatId = doc.id,
                        lastMessage = doc.getString("lastMessage") ?: "",
                        otherUserName = otherUserName
                    )
                }
                _chatList.value = chats
            }
    }
}
