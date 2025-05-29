package com.diplom.rande_vuz.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.Chat
import com.diplom.rande_vuz.ui.message.MessageFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class ChatListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatListAdapter
    private val currentUserId: String? get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        adapter = ChatListAdapter { chat ->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@ChatListAdapter
            val otherUserId = chat.userIds.firstOrNull { it != currentUserId } ?: currentUserId

            val action = MessageFragmentDirections.actionChatListToChat(
                chatId = chat.id,
                otherUserId = otherUserId
            )
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Загружаем чаты при первом открытии
        loadUserChats()

        // Добавляем слушатель для обновлений чатов в реальном времени
        val userId = currentUserId ?: return
        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // При любом изменении в структуре чатов перезагружаем список
                loadUserChats()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Ошибка слушателя чатов: ${error.message}")
            }
        }

        // Регистрируем слушатель
        chatsRef.addValueEventListener(valueEventListener)

        // Удаляем слушатель при уничтожении View
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                chatsRef.removeEventListener(valueEventListener)
            }
        })
    }

    private fun loadUserChats() {
        val userId = currentUserId ?: return
        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")

        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = mutableListOf<Chat>()
                val chatsToLoad = snapshot.children.filter { chatSnap ->
                    val userIds = chatSnap.child("userIds").children.mapNotNull { it.getValue(String::class.java) }
                    userIds.contains(userId)
                }

                if (chatsToLoad.isEmpty()) {
                    adapter.submitList(emptyList())
                    return
                }

                var loadedChats = 0

                for (chatSnapshot in chatsToLoad) {
                    val chatId = chatSnapshot.key ?: continue
                    val lastMessage = chatSnapshot.child("lastMessage").getValue(String::class.java) ?: ""
                    val timestamp = chatSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    val userIds = chatSnapshot.child("userIds").children.mapNotNull { it.getValue(String::class.java) }

                    val otherUserId = userIds.firstOrNull { it != userId } ?: userId

                    // Проверяем наличие непрочитанных сообщений
                    val hasUnread = chatSnapshot.child("messages").children.any { messageSnap ->
                        val read = messageSnap.child("read").getValue(Boolean::class.java) ?: true
                        val senderId = messageSnap.child("senderId").getValue(String::class.java)
                        !read && senderId != userId // Непрочитанные сообщения, которые не мы отправили
                    }

                    val userNames = mutableMapOf<String, String>()
                    val userPhotos = mutableMapOf<String, String?>()
                    val usersRef = FirebaseDatabase.getInstance().getReference("users")

                    var loadedUserProfiles = 0

                    for (uid in userIds) {
                        usersRef.child(uid).get().addOnSuccessListener { userSnap ->
                            val name = userSnap.child("name").getValue(String::class.java) ?: "Unknown"
                            val photoPath = userSnap.child("profilePhotoPath").getValue(String::class.java)

                            userNames[uid] = name
                            userPhotos[uid] = photoPath

                            loadedUserProfiles++
                            if (loadedUserProfiles == userIds.size) {
                                val chatName = userNames[otherUserId] ?: "Unknown"
                                val chatPhotoPath = userPhotos[otherUserId]

                                val chat = Chat(
                                    id = chatId,
                                    userIds = userIds,
                                    lastMessage = lastMessage,
                                    timestamp = timestamp,
                                    chatName = chatName,
                                    chatPhotoPath = chatPhotoPath,
                                    hasUnreadMessages = hasUnread // Передаем информацию о непрочитанных
                                )

                                chatList.add(chat)

                                loadedChats++
                                if (loadedChats == chatsToLoad.size) {
                                    adapter.submitList(chatList.sortedByDescending { it.timestamp })
                                }
                            }
                        }.addOnFailureListener {
                            loadedUserProfiles++
                            if (loadedUserProfiles == userIds.size) {
                                val chatName = userNames[otherUserId] ?: "Unknown"

                                val chat = Chat(
                                    id = chatId,
                                    userIds = userIds,
                                    lastMessage = lastMessage,
                                    timestamp = timestamp,
                                    chatName = chatName,
                                    chatPhotoPath = null,
                                    hasUnreadMessages = hasUnread // Передаем информацию о непрочитанных
                                )

                                chatList.add(chat)

                                loadedChats++
                                if (loadedChats == chatsToLoad.size) {
                                    adapter.submitList(chatList.sortedByDescending { it.timestamp })
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Ошибка загрузки чатов: ${error.message}")
            }
        })
    }
}