package com.diplom.rande_vuz

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.Logger
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        Firebase.database.setLogLevel(Logger.Level.DEBUG)

        try {
            val db = Firebase.database("https://randevuz-default-rtdb.europe-west1.firebasedatabase.app/")
            db.setPersistenceEnabled(true)
            Log.d("FIREBASE_INIT", "Database initialized successfully")
        } catch (e: Exception) {
            Log.e("FIREBASE_INIT", "Database initialization failed", e)
        }
    }
}