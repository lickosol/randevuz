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
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvWork: TextView = itemView.findViewById(R.id.tvWork)
        val tvSkills: TextView = itemView.findViewById(R.id.tvSkills)
        val tvExtra: TextView = itemView.findViewById(R.id.tvExtra)
        val tvGoal: TextView = itemView.findViewById(R.id.tvGoal)
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
        holder.tvDescription.text = user.description
        holder.tvWork.text = user.work
        holder.tvSkills.text = user.skills
        holder.tvExtra.text = user.extracurricular
        holder.tvGoal.text = user.goal
    }

    override fun getItemCount(): Int = userList.size

    fun updateUsers(newList: List<UserData>) {
        userList = newList
        notifyDataSetChanged()
    }
}
