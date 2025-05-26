package com.diplom.rande_vuz.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diplom.rande_vuz.databinding.ActivityEditProfileBinding
import com.diplom.rande_vuz.models.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val dbRef by lazy { FirebaseDatabase.getInstance().getReference("users") }
    private val uid by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProfile()
        binding.buttonSave.setOnClickListener { saveProfile() }
    }

    private fun loadProfile() {
        dbRef.child(uid!!).get()
            .addOnSuccessListener { snap ->
                val user = snap.getValue(UserData::class.java)
                user?.let {
                    binding.editTextName.setText(it.name)
                    binding.editTextBirthDate.setText(it.birthDate)
                    binding.editTextSpecialization.setText(it.specialization)
                    binding.editTextVuzName.setText(it.vuzName)
                    binding.editTextEmail.setText(it.email)
                    binding.editTextWork.setText(it.work)
                    binding.editTextExtracurricular.setText(it.extracurricular)
                    binding.editTextDescription.setText(it.description)
                    binding.editTextSkills.setText(it.skills)
                    val goalsString = when (val g = it.goal) {
                        is String -> g
                        is List<*> -> (g as List<String>).joinToString(", ")
                        else -> ""
                    }
                    binding.editTextGoals.setText(goalsString)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Не удалось загрузить профиль", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfile() {
        val updated = UserData(
            name = binding.editTextName.text.toString().trim(),
            birthDate = binding.editTextBirthDate.text.toString().trim(),
            specialization = binding.editTextSpecialization.text.toString().trim(),
            vuzName = binding.editTextVuzName.text.toString().trim(),
            email = binding.editTextEmail.text.toString().trim(),
            work = binding.editTextWork.text.toString().trim(),
            extracurricular = binding.editTextExtracurricular.text.toString().trim(),
            description = binding.editTextDescription.text.toString().trim(),
            skills = binding.editTextSkills.text.toString().trim(),
            goal = binding.editTextGoals.text.toString().split(",")
                .map(String::trim)
                .filter(String::isNotBlank)
        )

        dbRef.child(uid!!).setValue(updated)
            .addOnSuccessListener {
                Toast.makeText(this, "Профиль сохранён", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка при сохранении профиля", Toast.LENGTH_SHORT).show()
            }
    }
}
