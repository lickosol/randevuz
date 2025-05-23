package com.diplom.rande_vuz.ui.lenta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.rande_vuz.databinding.FragmentLentaBinding
import com.diplom.rande_vuz.models.UserData

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

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this)[LentaViewModel::class.java]

        // Инициализация адаптера с пустым списком
        adapter = LentaAdapter(emptyList())

        // Настройка RecyclerView
        binding.rvProfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfiles.adapter = adapter

        // Подписка на изменения данных
        viewModel.users.observe(viewLifecycleOwner) { userList ->
            if (userList.isNotEmpty()) {
                adapter.updateUsers(userList)
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
