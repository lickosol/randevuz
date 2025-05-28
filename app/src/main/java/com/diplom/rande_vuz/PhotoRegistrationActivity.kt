package com.diplom.rande_vuz.registration

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.activities.PasswordRegistrationActivity
import com.diplom.rande_vuz.models.UserData
import java.io.File
import java.io.FileOutputStream

class PhotoRegistrationActivity : AppCompatActivity() {

    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnUploadPhoto: Button
    private lateinit var btnNext: Button

    private var photoPath: String? = null
    private var userData: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_registration)

        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto)
        btnNext = findViewById(R.id.btnNext)

        userData = intent.getSerializableExtra("userData") as? UserData

        btnUploadPhoto.setOnClickListener {
            openGallery()
        }

        btnNext.setOnClickListener {
            if (photoPath != null) {
                userData?.profilePhotoPath = photoPath!!
                val intent = Intent(this, PasswordRegistrationActivity::class.java)
                intent.putExtra("userData", userData)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Пожалуйста, загрузите фото", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data!!.data?.let { saveImageLocally(it) }
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
            val bitmap = BitmapFactory.decodeFile(photoPath)
            ivProfilePhoto.setImageBitmap(bitmap)
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "profile_photo.jpg"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }
}
