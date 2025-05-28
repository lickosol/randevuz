package com.diplom.rande_vuz.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.diplom.rande_vuz.R
import com.diplom.rande_vuz.databinding.ActivityAfterRegistrationBinding
import com.diplom.rande_vuz.ui.chats.ChatListFragmentDirections.Companion.actionGlobalToChat

class AfterRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterRegistrationBinding
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAfterRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_after_registration)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_message,
                R.id.navigation_lenta,
                R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)
    }

    // Функция для переключения на вкладку мессенджера и открытия чата
    fun navigateToChatFromOtherTab(chatId: String?, otherUserId: String) {
        // 1. Проверяем входные данные
        if (otherUserId.isBlank()) {
            Log.e("NAV_ERROR", "otherUserId is empty!")
            return
        }

        // 2. Переключаемся на вкладку мессенджера
        navView.selectedItemId = R.id.navigation_message

        // 3. Ждем переключения таба и очищаем back stack
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_activity_after_registration) as NavHostFragment

                // Очищаем стек навигации мессенджера
                navHostFragment.navController.popBackStack(R.id.navigation_message, false)

                // Создаем аргументы
                val args = Bundle().apply {
                    putString("chatId", chatId)
                    putString("otherUserId", otherUserId)
                }

                // Навигация с очисткой стека
                navHostFragment.navController.navigate(
                    R.id.action_global_to_chat,
                    args,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_message, false)
                        .build()
                )

                Log.d("NAV_DEBUG", "Navigated to chat with $otherUserId")
            } catch (e: Exception) {
                Log.e("NAV_ERROR", "Failed to navigate", e)
            }
        }, 350) // Увеличили задержку для надежности
    }
}