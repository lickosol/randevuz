package com.diplom.rande_vuz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.diplom.rande_vuz.activities.AfterRegistrationActivity
import com.diplom.rande_vuz.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        checkCurrentUser()

    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            navigateToApp()
        } else {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToApp() {
        startActivity(Intent(this, AfterRegistrationActivity::class.java))
        finish()
    }
}