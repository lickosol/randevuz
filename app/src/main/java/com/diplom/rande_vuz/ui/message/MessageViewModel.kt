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

    fun sendMessage(chatId: String, messageText: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val timestamp = System.currentTimeMillis()

        val message = Message(
            content = messageText,
            senderId = userId,
            timestamp = timestamp,
            read = false
        )

        val chatRef = database.getReference("chats").child(chatId)
        val newMessageRef = chatRef.child("messages").push()
        val messageId = newMessageRef.key ?: return

        val updates = hashMapOf<String, Any>(
            "/messages/$messageId" to message,
            "/lastMessage" to messageText,
            "/timestamp" to timestamp
        )

        chatRef.updateChildren(updates)
            .addOnSuccessListener { _sendStatus.value = SendStatus.Success }
            .addOnFailureListener { exception ->
                _sendStatus.value = SendStatus.Error(exception)
            }
    }
}

