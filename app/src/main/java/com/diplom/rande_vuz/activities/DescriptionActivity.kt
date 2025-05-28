package com.diplom.rande_vuz.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.models.UserData
import com.diplom.rande_vuz.registration.PhotoRegistrationActivity

class DescriptionActivity : AppCompatActivity() {
    private lateinit var userData: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        userData = intent.getSerializableExtra("userData") as? UserData ?: UserData()

        val nextBtn = findViewById<Button>(R.id.next_btn)
        val descriptionInput = findViewById<EditText>(R.id.description_input)

        nextBtn.setOnClickListener {
            userData.description = descriptionInput.text.toString().trim()

            if (userData.description.isEmpty()) {
                Toast.makeText(this, "Поле не должно быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Intent(this, PhotoRegistrationActivity::class.java).apply {
                putExtra("userData", userData)
                startActivity(this)
            }
        }
    }
}