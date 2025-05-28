package com.diplom.rande_vuz.ui.lenta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.rande_vuz.activities.AfterRegistrationActivity
import com.diplom.rande_vuz.databinding.FragmentLentaBinding
import com.google.firebase.auth.FirebaseAuth

class LentaFragment : Fragment() {

    private var _binding: FragmentLentaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LentaViewModel
    private lateinit var adapter: LentaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Лог UID текущего пользователя
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("LentaFragment", "Текущий UID: ${currentUser?.uid}")

        // Инициализация адаптера с обработчиком клика по пользователю
        adapter = LentaAdapter(emptyList()) { user ->
            if (user.uid.isBlank()) {
                Toast.makeText(context, "Ошибка: ID пользователя пустой", Toast.LENGTH_SHORT).show()
                return@LentaAdapter
            }
            (activity as? AfterRegistrationActivity)?.navigateToChatFromOtherTab(
                chatId = null,
                otherUserId = user.uid
            )
        }

        binding.rvProfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfiles.adapter = adapter

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this)[LentaViewModel::class.java]

        // Подписка на обновление пользователей
        viewModel.users.observe(viewLifecycleOwner) { users ->
            Log.d("LentaFragment", "Получено пользователей: ${users.size}")
            if (users.isNotEmpty()) {
                adapter.updateUsers(users)
            } else {
                Toast.makeText(requireContext(), "Нет пользователей для отображения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
