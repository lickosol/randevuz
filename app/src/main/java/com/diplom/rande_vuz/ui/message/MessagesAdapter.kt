package com.diplom.rande_vuz.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.MessageDisplay
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MessagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<MessageDisplay>()
    private val TYPE_INCOMING = 0
    private val TYPE_OUTGOING = 1

    override fun getItemViewType(position: Int): Int {
        val msg = messages[position]
        val me = FirebaseAuth.getInstance().currentUser?.uid
        return if (msg.senderId == me) TYPE_OUTGOING else TYPE_INCOMING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_OUTGOING) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_outgoing, parent, false)
            OutgoingHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_incoming, parent, false)
            IncomingHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val m = messages[position]
        if (holder is IncomingHolder) holder.bind(m)
        else if (holder is OutgoingHolder) holder.bind(m)
    }

    override fun getItemCount(): Int = messages.size

    fun submitList(list: List<MessageDisplay>) {
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    inner class IncomingHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val tvText = item.findViewById<TextView>(R.id.textViewMessageContent)
        private val tvTime = item.findViewById<TextView>(R.id.textViewMessageTime)

        fun bind(m: MessageDisplay) {
            tvText.text = m.content
            tvTime.text = formatTimestamp(m.timestamp)
        }
    }

    inner class OutgoingHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val tvText   = item.findViewById<TextView>(R.id.textViewMessageContent)
        private val tvTime   = item.findViewById<TextView>(R.id.textViewMessageTime)
        private val tvStatus = item.findViewById<TextView>(R.id.textViewReadStatus)

        fun bind(m: MessageDisplay) {
            tvText.text   = m.content
            tvTime.text   = formatTimestamp(m.timestamp)
            tvStatus.text = if (m.read) "Прочитано" else "Непрочитано"
        }
    }

    private fun formatTimestamp(ts: Long): String {
        val date = Date(ts)
        val now  = Calendar.getInstance()
        val msgC = Calendar.getInstance().apply { time = date }

        return if (now.get(Calendar.YEAR) == msgC.get(Calendar.YEAR)
            && now.get(Calendar.DAY_OF_YEAR) == msgC.get(Calendar.DAY_OF_YEAR)
        ) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date)
        }
    }
}
