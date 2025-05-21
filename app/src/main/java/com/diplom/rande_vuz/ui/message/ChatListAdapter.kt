package com.diplom.rande_vuz.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.Chat
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter :
    ListAdapter<Chat, ChatListAdapter.ChatViewHolder>(ChatDiffCallback()) {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.chatUserName)
        val lastMessageTextView: TextView = view.findViewById(R.id.chatLastMessage)
        val messageDateTextView: TextView = view.findViewById(R.id.chatMessageDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        holder.usernameTextView.text = chat.chatName
        holder.lastMessageTextView.text = chat.lastMessage

        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateText = sdf.format(Date(chat.timestamp))

        holder.messageDateTextView.text = dateText
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Chat, newItem: Chat) = oldItem == newItem
}
