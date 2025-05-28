import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diplom.rande_vuz.models.Message
import com.diplom.rande_vuz.models.MessageDisplay
import com.diplom.rande_vuz.models.SendStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<MessageDisplay>>()
    val messages: LiveData<List<MessageDisplay>> = _messages

    private val _sendStatus = MutableLiveData<SendStatus>()
    val sendStatus: LiveData<SendStatus> = _sendStatus

    private val database = FirebaseDatabase.getInstance()
    private val userCache = mutableMapOf<String, String>()

    fun loadMessages(chatId: String) {
        val messagesRef = database.getReference("chats").child(chatId).child("messages")

        messagesRef.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<MessageDisplay>()

                val allMessages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }

                val dispatchMessages = {
                    _messages.value = tempList.sortedBy { it.timestamp }
                }

                var counter = allMessages.size
                if (counter == 0) {
                    _messages.value = emptyList()
                    return
                }

                allMessages.forEach { msg ->
                    getUserName(msg.senderId) { name ->
                        tempList.add(
                            MessageDisplay(
                                content = msg.content,
                                senderName = name,
                                timestamp = msg.timestamp
                            )
                        )
                        counter--
                        if (counter == 0) dispatchMessages()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun checkExistingChat(otherUserId: String, callback: (String?) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            callback(null)
            return
        }

        database.getReference("chats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (chatSnapshot in snapshot.children) {
                        val userIds = chatSnapshot.child("userIds").getValue<List<String>>()
                        if (userIds != null && userIds.containsAll(listOf(currentUserId, otherUserId))) {
                            callback(chatSnapshot.key)
                            return
                        }
                    }
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    fun sendMessage(chatId: String, messageText: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val timestamp = System.currentTimeMillis()

        val chatRef = database.getReference("chats").child(chatId)
        val messagesRef = chatRef.child("messages")
        val messageId = messagesRef.push().key ?: return

        // Полная структура сообщения
        val messageData = hashMapOf<String, Any>(
            "content" to messageText,
            "senderId" to userId,
            "timestamp" to timestamp,
            "read" to false,
            "id" to messageId
        )

        // Обновление чата
        val updates = hashMapOf<String, Any>(
            "messages/$messageId" to messageData,
            "lastMessage" to messageText,
            "timestamp" to timestamp
        )

        chatRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("MessageSend", "Сообщение отправлено в чат $chatId")
                _sendStatus.value = SendStatus.Success
            }
            .addOnFailureListener { e ->
                Log.e("MessageSend", "Ошибка отправки", e)
                _sendStatus.value = SendStatus.Error(e)
            }
    }

    fun createChatWithMessage(otherUserId: String, messageText: String, onChatCreated: (String) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Проверяем, что otherUserId не пустой
        if (otherUserId.isBlank()) {
            Log.e("CHAT_ERROR", "otherUserId is empty!")
            return
        }

        val timestamp = System.currentTimeMillis()
        val chatRef = database.reference.child("chats").push()
        val chatId = chatRef.key ?: return

        val messageId = chatRef.child("messages").push().key ?: return

        // Формируем строго заданную структуру
        val chatData = mapOf(
            "userIds" to listOf(currentUserId, otherUserId), // Обязательно в корне
            "lastMessage" to messageText,
            "timestamp" to timestamp,
            "messages" to mapOf(
                messageId to mapOf(
                    "content" to messageText,
                    "senderId" to currentUserId,
                    "timestamp" to timestamp,
                    "read" to false
                )
            )
        )

        // Логируем структуру перед отправкой

        chatRef.setValue(chatData)
            .addOnSuccessListener {
                Log.d("CHAT_SUCCESS", "Chat created with ID: $chatId")
                onChatCreated(chatId)
            }
            .addOnFailureListener { e ->
                Log.e("CHAT_ERROR", "Failed to create chat", e)
            }
    }
    private fun getUserName(userId: String, callback: (String) -> Unit) {
        userCache[userId]?.let {
            callback(it)
            return
        }

        database.getReference("users").child(userId).child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java) ?: "Неизвестный"
                    userCache[userId] = name
                    callback(name)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback("Неизвестный")
                }
            })
    }
}