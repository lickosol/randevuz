package com.diplom.rande_vuz.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diplom.rande_vuz.MainActivity
import com.diplom.rande_vuz.databinding.ActivityPasswordRegistrationBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.diplom.rande_vuz.models.UserData

class PasswordRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordRegistrationBinding
    private val auth = Firebase.auth
    private val database = Firebase.database("https://randevuz-default-rtdb.europe-west1.firebasedatabase.app/")
    private lateinit var userData: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userData = intent.getSerializableExtra("userData") as? UserData ?: UserData()

        binding.regbutton.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите почту и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userData.email = email
                    saveUserData()
                } else {
                    Toast.makeText(this, "Ошибка: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Ошибка: пользователь не создан", Toast.LENGTH_SHORT).show()
            return
        }

        val userMap = hashMapOf<String, Any>(
            "uid" to userId,
            "name" to (userData.name ?: ""),
            "birthDate" to (userData.birthDate ?: ""),
            "vuz_name" to (userData.vuzName ?: ""),
            "specialization" to (userData.specialization ?: ""),
            "skills" to (userData.skills ?: ""),
            "extracurricular" to (userData.extracurricular ?: ""),
            "work" to (userData.work ?: ""),
            "goal" to (userData.goal ?: emptyList<String>()),
            "description" to (userData.description ?: ""),
            "email" to (userData.email ?: "")
        )

        database.reference.child("users").child(userId).setValue(userMap)
            .addOnSuccessListener {
                val intent = Intent(this, AfterRegistrationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}