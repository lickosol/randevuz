package com.diplom.rande_vuz.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diplom.rande_vuz.databinding.ActivityEditProfileBinding
import com.diplom.rande_vuz.models.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val dbRef by lazy { FirebaseDatabase.getInstance().getReference("users") }
    private val uid by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    private var photoPath: String? = null

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

        binding.buttonChangePhoto.setOnClickListener {
            openGallery()
        }

        binding.buttonSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { saveImageLocally(it) }
        }
    }

    private fun saveImageLocally(uri: Uri) {
        val fileName = getFileName(uri)
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val file = File(filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            photoPath = file.absolutePath
            binding.imageViewProfile.setImageBitmap(BitmapFactory.decodeFile(photoPath))
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "updated_profile_photo.jpg"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
        return name
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

                    photoPath = it.profilePhotoPath
                    if (!photoPath.isNullOrBlank()) {
                        val bitmap = BitmapFactory.decodeFile(photoPath)
                        binding.imageViewProfile.setImageBitmap(bitmap)
                    }

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
            uid = uid!!,
            name = binding.editTextName.text.toString().trim(),
            birthDate = binding.editTextBirthDate.text.toString().trim(),
            specialization = binding.editTextSpecialization.text.toString().trim(),
            vuzName = binding.editTextVuzName.text.toString().trim(),
            email = binding.editTextEmail.text.toString().trim(),
            work = binding.editTextWork.text.toString().trim(),
            extracurricular = binding.editTextExtracurricular.text.toString().trim(),
            description = binding.editTextDescription.text.toString().trim(),
            skills = binding.editTextSkills.text.toString().trim(),
            goal = binding.editTextGoals.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() },
            profilePhotoPath = photoPath ?: ""
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
