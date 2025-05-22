package com.diplom.rande_vuz.ui.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
        recyclerView = view.findViewById(R.id.chatRecyclerView)
        adapter = ChatListAdapter()
        recyclerView.adapter = adapter

        loadUserChats()
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

                    val userNames = mutableMapOf<String, String>()
                    val usersRef = FirebaseDatabase.getInstance().getReference("users")

                    var loadedUserNames = 0

                    for (uid in userIds) {
                        usersRef.child(uid).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(nameSnapshot: DataSnapshot) {
                                val name = nameSnapshot.getValue(String::class.java) ?: "Unknown"
                                userNames[uid] = name

                                loadedUserNames++
                                if (loadedUserNames == userIds.size) {
                                    val otherUserId = userIds.firstOrNull { it != userId } ?: userId
                                    val chatName = userNames[otherUserId] ?: "Unknown"

                                    val chat = Chat(
                                        id = chatId,
                                        userIds = userIds,
                                        lastMessage = lastMessage,
                                        timestamp = timestamp,
                                        chatName = chatName
                                    )
                                    chatList.add(chat)

                                    loadedChats++
                                    if (loadedChats == chatsToLoad.size) {
                                        adapter.submitList(chatList.sortedByDescending { it.timestamp })
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                userNames[uid] = "Unknown"

                                loadedUserNames++
                                if (loadedUserNames == userIds.size) {
                                    val otherUserId = userIds.firstOrNull { it != userId } ?: userId
                                    val chatName = userNames[otherUserId] ?: "Unknown"

                                    val chat = Chat(
                                        id = chatId,
                                        userIds = userIds,
                                        lastMessage = lastMessage,
                                        timestamp = timestamp,
                                        chatName = chatName
                                    )
                                    chatList.add(chat)

                                    loadedChats++
                                    if (loadedChats == chatsToLoad.size) {
                                        adapter.submitList(chatList.sortedByDescending { it.timestamp })
                                    }
                                }
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Ошибка загрузки чатов: ${error.message}")
            }
        })
    }
}