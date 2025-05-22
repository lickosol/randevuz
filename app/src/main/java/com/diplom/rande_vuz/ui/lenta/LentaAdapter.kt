package com.diplom.rande_vuz.ui.lenta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.UserData

class LentaAdapter(private var userList: List<UserData>) :
    RecyclerView.Adapter<LentaAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAgeUniversity: TextView = itemView.findViewById(R.id.tvAgeUniversity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lenta, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = "${user.name}, ${user.specialization}"
        holder.tvAgeUniversity.text = "${user.birthDate}, ${user.vuzName}"
    }

    override fun getItemCount(): Int = userList.size
    fun updateUsers(newList: List<UserData>) {
        userList = newList
        notifyDataSetChanged()
    }
}
