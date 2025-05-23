package com.diplom.rande_vuz.ui.lenta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
        val tvGoal: LinearLayout = itemView.findViewById(R.id.tvGoal)

        // Контейнеры для скрытия
        val blockDescription: ViewGroup = tvDescription.parent as ViewGroup
        val blockWork: ViewGroup = tvWork.parent as ViewGroup
        val blockSkills: ViewGroup = tvSkills.parent as ViewGroup
        val blockExtra: ViewGroup = tvExtra.parent as ViewGroup
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lenta, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        // Имя и специальность
        holder.tvName.text = listOfNotNull(user.name, user.specialization).joinToString(", ")

        // Возраст и вуз
        holder.tvAgeUniversity.text = listOfNotNull(user.birthDate, user.vuzName).joinToString(", ")

        // Описание
        if (!user.description.isNullOrBlank()) {
            holder.tvDescription.text = user.description
            holder.blockDescription.visibility = View.VISIBLE
        } else {
            holder.blockDescription.visibility = View.GONE
        }

        // Работа
        if (!user.work.isNullOrBlank()) {
            holder.tvWork.text = user.work
            holder.blockWork.visibility = View.VISIBLE
        } else {
            holder.blockWork.visibility = View.GONE
        }

        // Навыки
        if (!user.skills.isNullOrBlank()) {
            holder.tvSkills.text = user.skills
            holder.blockSkills.visibility = View.VISIBLE
        } else {
            holder.blockSkills.visibility = View.GONE
        }

        // Внеучебная деятельность
        if (!user.extracurricular.isNullOrBlank()) {
            holder.tvExtra.text = user.extracurricular
            holder.blockExtra.visibility = View.VISIBLE
        } else {
            holder.blockExtra.visibility = View.GONE
        }

        // Цель
        holder.tvGoal.removeAllViews()
        val goalList = when (val goal = user.goal) {
            is String -> if (goal.isNotBlank()) listOf(goal) else emptyList()
            is List<*> -> goal.filterIsInstance<String>().filter { it.isNotBlank() }
            else -> emptyList()
        }

        for (goal in goalList) {
            val goalTextView = TextView(holder.tvGoal.context).apply {
                text = goal
                setTextAppearance(R.style.TagText)
                setPadding(8, 4, 8, 4)
            }
            holder.tvGoal.addView(goalTextView)
        }

        holder.tvGoal.visibility = if (goalList.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = userList.size

    fun updateUsers(newList: List<UserData>) {
        userList = newList
        notifyDataSetChanged()
    }
}
