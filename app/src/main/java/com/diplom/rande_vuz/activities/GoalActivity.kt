package com.diplom.rande_vuz.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.UserData
import com.google.android.material.button.MaterialButton

class GoalActivity : AppCompatActivity() {

    private lateinit var userData: UserData
    private var selectedGoal: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        userData = intent.getSerializableExtra("userData") as? UserData ?: UserData()

        val nextBtn = findViewById<MaterialButton>(R.id.next_btn)
        val goalWork = findViewById<MaterialButton>(R.id.goal_work)
        val goalFriendship = findViewById<MaterialButton>(R.id.goal_friendship)
        val goalTutoring = findViewById<MaterialButton>(R.id.goal_tutoring)
        val goalUnknown = findViewById<MaterialButton>(R.id.goal_unknown)

        val goals = listOf(goalWork, goalFriendship, goalTutoring)

        fun toggleButton(button: MaterialButton, isSelected: Boolean) {
            val color = if (isSelected) R.color.blue else R.color.pink
            button.setBackgroundColor(ContextCompat.getColor(this, color))
            button.tag = isSelected
        }

        goals.forEach { button ->
            button.setOnClickListener {
                toggleButton(goalUnknown, false)

                val wasSelected = button.tag as? Boolean ?: false
                toggleButton(button, !wasSelected)

                if (!wasSelected) {
                    selectedGoal = button.text.toString()
                } else {
                    selectedGoal = null
                }
            }
        }

        goalUnknown.setOnClickListener {
            goals.forEach { button ->
                toggleButton(button, false)
            }

            val wasSelected = goalUnknown.tag as? Boolean ?: false
            toggleButton(goalUnknown, !wasSelected)

            selectedGoal = if (!wasSelected) goalUnknown.text.toString() else null
        }

        nextBtn.setOnClickListener {
            if (selectedGoal == null) {
                Toast.makeText(this, "Выберите хотя бы одну цель", Toast.LENGTH_SHORT).show()
            } else {
                userData.goal = if (selectedGoal != null) {
                    listOf(selectedGoal!!)
                } else {
                    userData.goal
                }

                Intent(this, DescriptionActivity::class.java).apply {
                    putExtra("userData", userData)
                    startActivity(this)
                }
            }
        }

    }
}
