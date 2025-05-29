package com.diplom.rande_vuz.ui.lenta

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.UserData
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import java.io.File
import java.util.*

class LentaAdapter(
    private var userList: List<UserData>,
    private val onUserClick: (UserData) -> Unit
) : RecyclerView.Adapter<LentaAdapter.UserViewHolder>() {

    // Состояния развернутости для каждого элемента
    private val expandedStates = mutableMapOf<Int, Boolean>()

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivPhoto)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAgeUniversity: TextView = itemView.findViewById(R.id.tvAgeUniversity)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvWork: TextView = itemView.findViewById(R.id.tvWork)
        val tvSkills: TextView = itemView.findViewById(R.id.tvSkills)
        val tvExtra: TextView = itemView.findViewById(R.id.tvExtra)
        val tvGoal: com.google.android.material.chip.ChipGroup = itemView.findViewById(R.id.tvGoal)
        val btnMessage: MaterialButton = itemView.findViewById(R.id.btnMessage)
        val btnExpand: MaterialButton = itemView.findViewById(R.id.btnExpand)
        val expandableLayout: LinearLayout = itemView.findViewById(R.id.expandableLayout)
        val textViews = listOf(tvDescription, tvWork, tvSkills, tvExtra)

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
        val isExpanded = expandedStates[position] ?: false

        // Загрузка фото
        val file = File(user.profilePhotoPath ?: "")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            holder.ivPhoto.setImageBitmap(bitmap)
        } else {
            holder.ivPhoto.setImageResource(R.drawable.black)
        }

        // Имя и специальность
        holder.tvName.text = listOfNotNull(user.name, user.specialization).joinToString(", ")

        // Возраст и вуз
        val age = calculateAge(user.birthDate)
        holder.tvAgeUniversity.text = listOfNotNull(age, user.vuzName).joinToString(", ")

        // Установка текстовых полей
        setupTextView(holder.tvDescription, user.description, "Описание: ")
        setupTextView(holder.tvWork, user.work, "Место работы: ")
        setupTextView(holder.tvSkills, user.skills, "Навыки: ")
        setupTextView(holder.tvExtra, user.extracurricular, "Внеучебная деятельность: ")

        // Обработка целей (чипы)
        setupGoals(holder, user)

        // Кнопка сообщения
        holder.btnMessage.setOnClickListener { onUserClick(user) }

        // Обработка разворачивания/сворачивания
        setupExpansion(holder, position, isExpanded)
    }

    override fun getItemCount(): Int = userList.size

    fun updateUsers(newList: List<UserData>) {
        userList = newList
        expandedStates.clear()
        notifyDataSetChanged()
    }

    // Helper: проверяет, есть ли ellipsis в любом ряду текста
    private fun isTextEllipsized(textView: TextView): Boolean {
        val layout = textView.layout ?: return false
        for (i in 0 until layout.lineCount) {
            if (layout.getEllipsisCount(i) > 0) return true
        }
        return false
    }

    private fun setupTextView(textView: TextView, text: String?, prefix: String) {
        if (!text.isNullOrBlank()) {
            val spannable = buildSpannedString {
                color(ContextCompat.getColor(textView.context, R.color.blue)) {
                    bold { append(prefix) }
                }
                append(text)
            }
            textView.text = spannable
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun setupGoals(holder: UserViewHolder, user: UserData) {
        holder.tvGoal.removeAllViews()
        val goalList = when (val goal = user.goal) {
            is String -> if (goal.isNotBlank()) listOf(goal) else emptyList()
            is List<*> -> goal.filterIsInstance<String>().filter { it.isNotBlank() }
            else -> emptyList()
        }

        for (goal in goalList) {
            val chip = Chip(holder.tvGoal.context).apply {
                text = goal
                isClickable = false
                isCheckable = false
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.white)
                )
                chipStrokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.soft)
                )
                setTextColor(ContextCompat.getColor(context, R.color.primary))
                val d = resources.displayMetrics.density
                setPadding((12 * d).toInt(), (4 * d).toInt(), (12 * d).toInt(), (4 * d).toInt())
            }
            holder.tvGoal.addView(chip)
        }
        holder.tvGoal.visibility = if (goalList.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupExpansion(holder: UserViewHolder, position: Int, isExpanded: Boolean) {
        if (isExpanded) {
            // Полностью развернуть
            holder.textViews.forEach {
                it.maxLines = Integer.MAX_VALUE
                it.ellipsize = null
            }
            holder.btnExpand.text = "Свернуть "
            holder.btnExpand.setIconResource(R.drawable.ic_expand_less)
            holder.btnExpand.visibility = View.VISIBLE
        } else {
            // Установить состояние свернутости
            holder.textViews.forEach {
                it.maxLines = 2
                it.ellipsize = android.text.TextUtils.TruncateAt.END
            }
            holder.btnExpand.text = "Показать ещё "
            holder.btnExpand.setIconResource(R.drawable.ic_expand_more)

            // Отложенная проверка после layout, чтобы увидеть, есть ли обрезка
            holder.expandableLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    holder.expandableLayout.viewTreeObserver.removeOnPreDrawListener(this)
                    val shouldShow = holder.textViews.any { it.visibility == View.VISIBLE && isTextEllipsized(it) }
                    holder.btnExpand.visibility = if (shouldShow) View.VISIBLE else View.GONE
                    return true
                }
            })
        }

        holder.btnExpand.setOnClickListener {
            val current = expandedStates[position] ?: false
            expandedStates[position] = !current
            notifyItemChanged(position)
        }
    }

    private fun calculateAge(birthDate: String): String? {
        return try {
            val parts = birthDate.split("/")
            if (parts.size != 3) return null
            val day = parts[0].toIntOrNull() ?: return null
            val monthStr = parts[1].lowercase(Locale.getDefault())
            val year = parts[2].toIntOrNull() ?: return null

            val monthMap = mapOf(
                "январь" to 1, "февраль" to 2, "март" to 3, "апрель" to 4,
                "май" to 5, "июнь" to 6, "июль" to 7, "август" to 8,
                "сентябрь" to 9, "октябрь" to 10, "ноябрь" to 11, "декабрь" to 12
            )
            val month = monthMap[monthStr] ?: return null

            val today = Calendar.getInstance()
            val birth = Calendar.getInstance().apply { set(year, month - 1, day) }

            var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) age--

            "$age " + when {
                age % 10 == 1 && age % 100 != 11 -> "год"
                age % 10 in 2..4 && age % 100 !in 12..14 -> "года"
                else -> "лет"
            }
        } catch (e: Exception) {
            null
        }
    }
}
