package com.diplom.rande_vuz.ui.chats

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
import android.graphics.BitmapFactory
import android.widget.ImageView


class ChatListAdapter(
    private val onChatClick: (Chat) -> Unit
) : ListAdapter<Chat, ChatListAdapter.ChatViewHolder>(ChatDiffCallback()) {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.chatUserName)
        val lastMessageTextView: TextView = view.findViewById(R.id.chatLastMessage)
        val messageDateTextView: TextView = view.findViewById(R.id.chatMessageDate)
        val avatarImageView: ImageView = view.findViewById(R.id.imgAvatar)
        val msgStatus: ImageView = view.findViewById(R.id.msgStatus)


        init {
            view.setOnClickListener {
                val chat = getItem(adapterPosition)
                onChatClick(chat)
            }
        }
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

        holder.messageDateTextView.text = formatTimestamp(chat.timestamp)

        // Устанавливаем видимость точки в зависимости от наличия непрочитанных сообщений
        holder.msgStatus.visibility = if (chat.hasUnreadMessages) View.VISIBLE else View.GONE

        chat.chatPhotoPath?.let { path ->
            val bitmap = BitmapFactory.decodeFile(path)
            if (bitmap != null) {
                holder.avatarImageView.setImageBitmap(bitmap)
            } else {
                holder.avatarImageView.setImageResource(R.drawable.ic_profile_placeholder)
            }
        } ?: run {
            holder.avatarImageView.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }


    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val now = Calendar.getInstance()
        val msgCal = Calendar.getInstance().apply { time = date }

        return if (
            now.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)
        ) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date)
        }
    }

}

class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Chat, newItem: Chat) = oldItem == newItem
}