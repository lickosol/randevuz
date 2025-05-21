package com.diplom.rande_vuz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.diplom.rande_vuz.activities.AfterRegistrationActivity
import com.diplom.rande_vuz.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//все что в этом коде закомменчено отвечает за то чтобы пользователя сразу кидало в ленту если он уже входил
//я пока что заккоментила чтоб было удобно переделывать разметку реги но когда 100% рега будет готова надо вернуть

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        checkCurrentUser()

//        navigateToLogin() //это наоборот потом надо будет убрать
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